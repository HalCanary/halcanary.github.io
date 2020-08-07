#!/usr/bin/env python
# coding=utf-8

import sys, os, math, subprocess, re, datetime, tempfile, hashlib, shutil

base_dir = 'vv'
url_base = 'https://halcanary.org'

relurlfixer = re.compile('^{}/(.*)$'.format(base_dir))

first_year = '1997'
current_year = '{:04d}'.format(datetime.date.today().year)

thisdir  = os.path.dirname(__file__)
with open(thisdir + '/style.css') as f:
    style = re.sub(r'(^|:)  *', r'\1', f.read(), flags=re.M).strip()

footer = """
<!-- END CONTENT -->

      </div>
      <hr class="black">

      <p>(<a href="../../../../archives/">back</a>)</p>
  </body>
</html>
"""

header = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>{title}</title>
<link rel="icon" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACAAQAAAADrRVxmAAAACXBIWXMAAAMfAAADHwHmEQywAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAVZJREFUSMfd1EuOwyAMAFAQ0rAZNUfIUTga5GbsZtkrcINhyQLBYMgHG6ZddJcoSpOXDzY2ZZls7F5Q9iAzhyu/XsAomCm4AdRHYPUUNAH7GTgdRbZd+k6FCZgGZQPw/DV45dgIWw+rc3rrRvGrHcBrjmDzqocAIBCIsr+ERcYFwYNAfMj0QPC9pC+JIWNgpfJvQGXTQwJYCFgMegSHIWPIA/DaplO49UIuEy7qUR5gWakJHA+oXZhNZYBYQNQnlgaBKSthrRjRwJXfcu+ZA28A5fmF7yXWwKg+BgB9AJyxPtgGib8FQSCKjEcZQR7XqUUaTgiCgJME9vRPMEwREJk+McCKodUBDUvjiDTSfU4v2NPvwCqc7QHif3Bz6Ga9vZIYga5yVs9rO1Qf+uOnSw6yhnKu2Yurx+C/Ru8TVLuQl9HPtmx9CqCvTlYwxxz3eqhv3Htl/wEokJpySHNGkgAAAABJRU5ErkJggg==">
<style>
{style}
</style>
</head>
  <!-- Copyright {years} Hal Canary. ALL RIGHTS RESERVED. -->
"""

top_posts_header = """
  <body>
      <h1>Voder-Vocoder</h1>
      <div class="byline centered">The Log of Hal Canary</div>

      <script>
        function search_hco() {
          document.getElementById("search-link-halcanary-org").style.display = "none";
          document.getElementById("search-halcanary-org").style.display = "block";
          return false;
        }
      </script>
      <div id="search-link-halcanary-org">
        <div class="rightside"><a href="" onclick="return search_hco();">search</a></div>
      </div>
      <div id="search-halcanary-org" style="display:none;">
        <form method="get" action="https://www.google.com/search">
          <div class="rightside">
            <input name="domains" value="halcanary.org" type="hidden" />
            <input name="sitesearch" value="halcanary.org" type="hidden" />
            <input id="search" name="q" size="30" maxlength="255" />
            <input value="Search" type="submit" />
          </div>
        </form>
      </div>
      <div class="postbox">&nbsp;</div>
      <div>
"""
top_posts_footer = """
      </div>
      <div class="centered"><a href="archives/">MORE</a></div>
      <div>&nbsp;</div>
  </body>
</html>
"""

def hashpath(path):
    if path is None:
        return None
    h = hashlib.sha256()
    with open(path,'r') as f:
        data = f.read(h.block_size)
        while data != '':
            h.update(data)
            data = f.read(h.block_size)
    return h.digest()
def mkdirs(path):
    try:
        os.makedirs(path)
    except:
        pass
class UpdatingFile(object):
    def __init__(self, path):
        self.tempfilepath = None
        self.path = path
        self.o = None
        path_dir = os.path.dirname(path)
        mkdirs(path_dir)
        fd, self.tempfilepath = tempfile.mkstemp()
        self.o = os.fdopen(fd, "w")
    def __del__(self):
        self.close()
    def close(self):
        if self.o is not None:
            self.o.close()
            self.o = None
            if (not os.path.isfile(self.path)) or (
                hashpath(self.tempfilepath) !=
                hashpath(self.path)):
                if shutil:
                    shutil.copyfile(self.tempfilepath, self.path)
                os.remove(self.tempfilepath)
                return True
            else:
                if self.tempfilepath is not None:
                    os.remove(self.tempfilepath)
                return False
    def write(self,*args, **kwargs):
        if self.o is not None:
            self.o.write(*args, **kwargs)
        else:
            sys.stderr.write(*args, **kwargs)

post_header = """
  <body>
      <h1 class="blogtitle">{title}</h1>
{summarypart}
      <div class="byline">By Hal Canary,
        {date}
        (<a href="{permalink}">link</a>)
{category_links}        </div>
      <!-- SRC= {src} -->
      <div class="content">

<!-- BEGIN CONTENT -->
"""

class Post(object):
    @staticmethod
    def markdown(inf):
        try:
            subproc = subprocess.Popen(
                ['cmark', '--unsafe'], stdin=subprocess.PIPE, stdout=subprocess.PIPE)
        except OSError:
            sys.stderr.write('\nMARKDOWN FAILED.\n\n')
            sys.exit(1)
        shutil.copyfileobj(inf, subproc.stdin)
        subproc.stdin.close()
        s = subproc.stdout.read()
        subproc.wait()
        return s
    def __init__(self, path):
        # Required:
        #    TITLE
        #    DATE
        #    POSTID
        # Suggested:
        #    SUMMARY
        #    CATEGORIES
        #    MODE
        # Not suggested, but used:
        #    PERMALINK
        #    PERMALINKPART
        # Everything else is ignored
        if not os.path.isfile(path):
            raise Exception(path+" is not a file")
        self.source_path = path
        self.src = os.path.basename(path)

        self.current_year = '{:04d}'.format(datetime.date.today().year)

        self.title = None
        self.date = None
        self.postid = None

        self.permalink = None
        self.permalinkpart = None

        self.summary = None
        self.summarypart = ''
        self.categories = ''
        self.mode = 'default'
        self.style = style

        #self.values = {}
        with open(self.source_path,'r') as f:
            while True:
                sline = f.readline().strip()
                if sline == '':
                    break
                if '=' not in sline:
                    print sline ## error
                    continue
                key, value = sline.split('=',1)
                value = value.strip()
                if value == '':
                    continue
                if key == 'TITLE':
                    self.title = value
                elif key == 'DATE':
                    self.date = value
                elif key == 'POSTID':
                    self.postid = value
                elif key == 'SUMMARY':
                    self.summary = value
                elif key == 'CATEGORIES':
                    self.categories = value
                elif key == 'MODE':
                    self.mode = value
                elif key != 'COPYRIGHT':
                    print '%s: %r=%r' % (path, key, value)

            if self.date is None:
                raise Exception('DATE missing ({})'.format(self.source_path))
            if self.postid is None:
                raise Exception('POSTID missing ({})'.format(self.source_path))
            if self.title is None:
                raise Exception('TITLE missing ({})'.format(self.source_path))

            if self.summary is not None:
                self.summarypart = (
                    '\n      <p>'
                    + self.summary + '</p>\n')
            else:
                self.summarypart = ''

            timestamp = datetime.datetime.strptime(
                self.date.split()[0],'%Y-%m-%d').date()
            self.year = '{:04d}'.format(timestamp.year)
            self.month = '{:02d}'.format(timestamp.month)
            self.day = '{:02d}'.format(timestamp.day)

            self.permalinkpart = '{}/{}/{}/{}/{}/'.format(
                    base_dir,self.year,self.month,self.day,
                    self.postid)

            self.permalink = url_base + '/' + self.permalinkpart
            self.target_file = self.permalinkpart + 'index.html'

            self.category_list = filter(len, self.categories.split(';'))
            self.category_links = ''
            if (len(self.category_list) > 0):
                cfmt = ('<a href="../../../../category/{0}/" ' +
                    'class="categorylink">#{0}</a>')
                self.category_links = ';'.join(cfmt.format(cat)
                                           for cat in self.category_list)
                self.category_links = '    <br>{}\n'.format(
                    self.category_links)

            o = UpdatingFile(self.target_file)
            o.write(header.format(title='Voder-Vocoder: ' + self.title, style=style, years=self.year + '-' + self.current_year))
            o.write(post_header.format(**self.__dict__))

            if self.mode.lower() == 'markdown':
                self.post = Post.markdown(f)
            else:
                self.post = f.read()
            o.write(self.post)
            o.write(footer)
            if o.close():
                print 'modified', self.target_file

recent_post = """
<div class="postbox">
      <h2 class="blogtitle">{title}</h2>
{summarypart}
      <div class="byline">By Hal Canary,
        {date}
        (<a href="{link}">link</a>)
        {category_links}
      </div>
      <!-- SRC={src} -->
      <div class="content">
{post}
      </div>
</div>

"""
class RecentPosts(object):
    def __init__(self):
        self.loc = '{}/index.html'.format(base_dir)
        self.o = UpdatingFile(self.loc)
        self.o.write(header.format(style=style, title='Voder-Vocoder', years='1997-' + current_year))
        self.o.write(top_posts_header)
    def close(self):
        self.o.write(top_posts_footer)
        if self.o.close():
            print 'modified', self.loc
    def addPost(self,post):
        relurlfixer = re.compile('^{}/(.*)$'.format(base_dir))
        relurl = relurlfixer.match(post.permalinkpart).groups()[0]
        self.o.write(
            recent_post.format(
                title=post.title,
                summarypart=post.summarypart,
                date=post.date,
                link=relurl,
                category_links=post.category_links,
                post=post.post,
                src=post.src))



class Index(object):
    @staticmethod
    def gethash(arg):
        return hash('{}/{}/index.html'.format(base_dir, '/'.join(arg)))
    def __hash__(self):
        return hash(self.loc)
    def __init__(self, arg, name=''):
        self.posts = []
        '''
        arg is in the format
            ['category','?*']
            ['????','??']
            ['????']
        '''
        if len(name) > 0:
            self.name = '<br>'+name
        else:
            self.name = ''
        self.upwards = '../' * len(arg)
        self.loc = '{}/{}/index.html'.format(base_dir, '/'.join(arg))
        self.addyear = False

    def addPost(self, post, addyear = False):
        d = vars(post)
        keys = [ 'permalinkpart', 'year', 'title', 'summary', 'date' ]
        self.posts += [{k: d.get(k) for k in keys}]
        self.addyear = addyear

    def close(self):
        o = UpdatingFile(self.loc)
        o.write(header.format(style=style, title='Voder-Vocoder Archive', years='1997-' + current_year))
        archive_header = """
  <body>
    <h1><a href="/vv/" class="hiddenlink">Voder-Vocoder</a> Archive{name}</h1>
    <div class="content">
<!-- BEGIN CONTENT -->
"""
        o.write(archive_header.format(name=self.name))
        lastyear = None
        for post in self.posts:
            relurlfixer = re.compile('^{}/(.*)$'.format(base_dir))
            short = relurlfixer.match(post['permalinkpart']).groups()[0]
            relurl = self.upwards + short
            assert post['year'] is not None
            if lastyear is not None and lastyear != post['year']:
                lastyear = None
                o.write('    </ul>\n')
            if lastyear is None:
                lastyear = post['year']
                if self.addyear:
                    o.write('<h2>{year}</h2>'.format(year=post['year']))
                o.write('    <ul>\n')

            o.write(
                '<li>\n  <a href="{relurl}">{title}</a>\n  {date}\n'.format(
                    relurl=relurl,title=post['title'],date=post['date']))
            if post['summary'] is not None:
                o.write('  <br>{}\n'.format(post['summary']))
            o.write('</li>\n')

        archive_footer = """
<!-- END CONTENT -->
    </div>
    <hr class="black">
    <p class="centered"><a href="../">UP</a></p>
  </body>
</html>
"""
        if lastyear is not None:
            o.write('    </ul>\n')
        o.write(archive_footer)
        if o.close():
            print 'modified', self.loc

def listdir(path, reverse=False):
    assert os.path.isdir(path)
    files = os.listdir(path)
    files.sort(reverse=reverse)
    for fn in files:
        yield os.path.join(path, fn)

def findIndex(indices, arg, name=''):
    h = Index.gethash(arg)
    if h in indices:
        return indices[h]
    else:
        n = Index(arg, name)
        indices[h] = n
        return n

if __name__ == '__main__':
    src_dir = os.path.abspath(os.path.dirname(__file__))
    target_location = os.path.join(src_dir, os.pardir)
    source_location = os.path.join(src_dir, 'BlogSrc')

    directory = source_location
    os.chdir(target_location)
    assert os.path.isdir(directory)
    indices = {}
    recentPosts = RecentPosts()
    for i, path in enumerate(listdir(directory, reverse=True)):
        if path[-1] == '~':
            continue
        if os.path.isfile(path):
            post = Post(path)
            if i < 10:
                recentPosts.addPost(post)
            findIndex(indices, ['archives'], '').addPost(post, True)
            findIndex(indices, [post.year], post.year).addPost(post)
            findIndex(indices, [post.year, post.month],
                      post.year+'-'+post.month).addPost(post)
            findIndex(indices, [post.year, post.month, post.day],
                      post.year+'-'+post.month+'-'+post.day).addPost(post)
            for cat in post.category_list:
                findIndex(indices, ['category',cat],'#'+cat).addPost(post)

        else:
            print "????", path
    recentPosts.close()
    for index in indices.itervalues():
        index.close()
