"""Prepare one trend brief per day for the NTS video review queue."""

import argparse
from datetime import datetime, timedelta, timezone
from email.utils import parsedate_to_datetime
import json
from pathlib import Path
from os import link
import re
from tempfile import NamedTemporaryFile
from urllib.error import URLError
from urllib.request import urlopen
from xml.etree import ElementTree
from zoneinfo import ZoneInfo

SITE = "https://narayanadritechnology.com/"
TRENDS = "https://trends.google.com/trending?geo=IN"
FEED = "https://trends.google.com/trending/rss?geo=IN"
FEED_LIMIT = 2_000_000
COURSES = (
    ("Java Full Stack", ("java full stack", "spring boot", "openjdk"),
     "Show how a registration form sends data to a Spring Boot API."),
    ("Python Full Stack", ("python", "django", "fastapi"),
     "Show how a Python API validates a form submission."),
    ("Software Testing", ("software testing", "playwright", "selenium"),
     "Show a browser test verifying a login form's validation message."),
    ("Data Analytics", ("data analytics", "power bi", "sql tutorial"),
     "Turn a raw enquiry spreadsheet into a chart of weekly totals."),
    ("Business Analysis", ("business analyst", "business analysis"),
     "Turn a vague registration requirement into two acceptance criteria."),
    ("Cloud and DevOps", ("devops", "kubernetes", "aws certification", "docker"),
     "Show how the same small app runs locally and inside a container."),
)


def select_topic(xml, now, recent_angles):
    root = ElementTree.fromstring(xml)
    if root.tag != "rss" or root.find("channel") is None:
        raise ValueError("expected an RSS feed")
    candidates = []
    for item in root.findall("./channel/item"):
        title = item.findtext("title", "").strip()
        raw_date = item.findtext("pubDate", "")
        try:
            published = parsedate_to_datetime(raw_date)
        except (ValueError, TypeError, OverflowError):
            continue
        if published.tzinfo is None:
            published = published.replace(tzinfo=timezone.utc)
        if not now - timedelta(days=2) <= published <= now:
            continue
        for course, keywords, angle in COURSES:
            matches = [word for word in keywords
                       if re.search(r"(?<!\w)" + re.escape(word) + r"(?!\w)", title.casefold())]
            if matches and angle not in recent_angles:
                candidates.append((max(map(len, matches)), published, title, course, angle))
    if not candidates:
        return None
    _, published, topic, course, angle = max(candidates)
    return {
        "topic": topic, "course": course, "demo": angle, "topic_type": "trend_signal",
        "source": TRENDS, "signal_published_at": published.isoformat(),
    }


def prepare_draft(xml, now, history):
    local_day = now.astimezone(ZoneInfo("Asia/Kolkata")).date()
    recent = {
        item["demo"] for item in history
        if (local_day - timedelta(days=5)).isoformat() <= item["date"] < local_day.isoformat()
    }
    topic = select_topic(xml, now, recent)
    if topic is None:
        course, _, angle = next(entry for entry in COURSES if entry[2] not in recent)
        topic = {"topic": course, "course": course, "demo": angle, "topic_type": "evergreen"}
    course = topic["course"]
    demo = topic["demo"]
    return {
        "date": local_day.isoformat(),
        "status": "DRAFT",
        **topic,
        "duration_seconds": 30,
        "aspect_ratio": "9:16",
        "voice_direction": "Conversational Indian English. Preview the pronunciation of Narayanadri.",
        "shots": [
            {"start": 0, "end": 4, "visual": "Open on a moving screen capture of the finished demo.",
             "subtitle": f"Try this {course} mini-project"},
            {"start": 4, "end": 24, "visual": demo,
             "subtitle": "Follow the input, the action, and the result."},
            {"start": 24, "end": 30, "visual": "Show NTS branding and website.",
             "subtitle": "Ask NTS about a free demo class"},
        ],
        "voiceover_draft": (
            f"Want to practise {course}? Start with a small demo. "
            f"{demo.replace('Show how ', 'Watch how ', 1).replace('Show a ', 'Watch a ', 1)} "
            "Pause at each step and explain what goes in, what happens, and what comes out. "
            "Ask Narayanadri Technology Solutions about a free demo class."
        ),
        "instagram_caption": (
            f"Try a {course} mini-project today. {demo} "
            "DM NTS for free demo class details."
        ),
        "linkedin_caption": (
            f"Students exploring {course} can practise explaining a real workflow with one small project. "
            f"{demo} Then explain the input, the decision, and the result. "
            f"Ask NTS about a free demo class at {SITE}"
        ),
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", required=True, type=Path, help="Folder for dated review drafts")
    parser.add_argument("--feed-file", type=Path, help="Use a local RSS snapshot for testing")
    args = parser.parse_args()
    now = datetime.now(timezone.utc)
    today = now.astimezone(ZoneInfo("Asia/Kolkata")).date()
    target = args.output / f"{today}.json"
    if target.exists():
        print(f"Already drafted: {target}")
        return
    try:
        if args.feed_file:
            with args.feed_file.open("rb") as feed:
                xml = feed.read(FEED_LIMIT + 1)
        else:
            with urlopen(FEED, timeout=20) as response:
                xml = response.read(FEED_LIMIT + 1)
        if len(xml) > FEED_LIMIT:
            raise ValueError("RSS feed exceeds the size limit")
        history = []
        for days_ago in range(1, 6):
            prior = args.output / f"{today - timedelta(days=days_ago)}.json"
            if prior.is_file():
                history.append(json.loads(prior.read_text(encoding="utf-8")))
        draft = prepare_draft(xml, now, history)
    except (URLError, OSError, ValueError, ElementTree.ParseError) as error:
        parser.exit(1, f"Could not prepare a draft: {error}\n")
    args.output.mkdir(parents=True, exist_ok=True)
    temporary = None
    try:
        with NamedTemporaryFile(mode="w", encoding="utf-8", dir=args.output,
                                delete=False) as output:
            temporary = Path(output.name)
            json.dump(draft, output, ensure_ascii=False, indent=2)
            output.write("\n")
        link(temporary, target)
    except FileExistsError:
        print(f"Already drafted: {target}")
        return
    finally:
        if temporary is not None:
            temporary.unlink()
    print(f"Created {draft['topic_type']} draft: {target}")


if __name__ == "__main__":
    main()
