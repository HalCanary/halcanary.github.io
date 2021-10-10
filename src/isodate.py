#! /usr/bin/env python3
import datetime
now = datetime.datetime.now().astimezone().replace(microsecond=0)
print('%s (%s)' % (now.isoformat(' '), now.tzinfo))
