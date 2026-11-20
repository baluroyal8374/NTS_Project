# NTS marketing agent

The first milestone prepares a daily video brief from Google Trends India. It matches recent search signals against NTS course topics and falls back to a clearly labelled evergreen idea when there is no match. A trend signal is a candidate for editorial review. It does not verify a technical claim. Drafts include a short script, shot directions, subtitles, and separate Instagram and LinkedIn captions.

## Run the worker

Use Python 3.9 or newer with access to the IANA time zone database and Google Trends. From the repository root:

```sh
python3 marketing-agent/draft.py --output /path/to/nts-review-drafts
```

Set `--feed-file` to an RSS snapshot for a local trial. Configure any daily scheduler to run the command in the same output directory. The worker uses the India calendar date and keeps one draft per day. If the feed is unavailable or malformed, the command exits with an error so a scheduler can report the failure and retry.

The worker currently uses topic templates. Video rendering, voice generation, a review screen, and publishing integration are pending. The approval step will apply to the finished video and both captions before scheduling them for the connected NTS accounts.
