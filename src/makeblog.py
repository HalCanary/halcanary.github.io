#!/usr/bin/env python

import sys, os, math, subprocess, re, datetime, tempfile, hashlib, shutil

base_dir = 'vv'
url_base = 'http://halcanary.org'

relurlfixer = re.compile('^{}/(.*)$'.format(base_dir))

first_year = '1997'
current_year = '{:04d}'.format(datetime.date.today().year)

footer = """
<!-- END CONTENT -->

      </div>
      <hr style="border:0;height:1px;color:#000;background-color:#000;">

      <p>(<a href="../../../../archives/">back</a>)</p>
    </div>
  </body>
</html>
"""

archive_header = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Voder-Vocoder Archive</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body {{
font-family:sans-serif;
}}
.column {{
max-width:35em;
margin:0 auto;
}}
img {{
max-width:100%;
height:auto;
}}
</style>
</head>
  <!-- Copyright 1997-{current_year} Hal Canary. ALL RIGHTS RESERVED. -->

  <body>
    <div class="column">

      <h1><a href="/vv/" class="hiddenlink">Voder-Vocoder</a> Archive{name}</h1>

      <div class="content">
      <ul>
<!-- BEGIN CONTENT -->
"""
archive_footer = """
<!-- END CONTENT -->

      </ul>
      </div>
      <hr style="border:0;height:1px;color:#000;background-color:#000;">
      <p class="centered"><a href="../">UP</a></p>
    </div>
  </body>
</html>
"""

top_posts_header = """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Voder-Vocoder</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body {{
font-family:sans-serif;
}}
.column {{
max-width:35em;
margin:0 auto;
}}
img {{
max-width:100%;
height:auto;
}}
</style>
</head>
  <!-- Copyright 1997-{current_year} Hal Canary. ALL RIGHTS RESERVED. -->

  <body>
    <div class="column">

      <h1>Voder-Vocoder</h1>
      <div class="byline centered">The Log of Hal Canary</div>

      <script>
        function search_hco() {{
          document.getElementById("search-link-halcanary-org").style.display = "none";
          document.getElementById("search-halcanary-org").style.display = "block";
          return false;
        }}
      </script>
      <div id="search-link-halcanary-org">
        <div style="text-align:right"><a href="" onclick="return search_hco();">search</a></div>
      </div>
      <div id="search-halcanary-org" style="display:none;">
        <form method="get" action="http://www.google.com/search">
          <div style="text-align:right">
            <input name="domains" value="halcanary.org" type="hidden" />
            <input name="sitesearch" value="halcanary.org" type="hidden" />
            <input id="search" name="q" size="30" maxlength="255" />
            <input value="Search" type="submit" />
          </div>
        </form>
      </div>
      <div style="margin-bottom:12px;">
"""
top_posts_footer = """
      </div>
      <div class="centered"><a href="archives/">MORE</a></div>
      <div style="margin-bottom:12px;">&nbsp;</div>
    </div>
  </body>
</html>
"""

class UpdatingFile(object):
    @staticmethod
    def mkdirs(path):
        try:
            os.makedirs(path)
        except:
            pass
    @staticmethod
    def hashpath(path):
        h = hashlib.sha256()
        with open(path,'r') as f:
            data = f.read(h.block_size)
            while data != '':
                h.update(data)
                data = f.read(h.block_size)
        return h.digest()

    def __init__(self, path):
        self.path = path
        path_dir = os.path.dirname(path)
        UpdatingFile.mkdirs(path_dir)
        fd, self.tempfilepath = tempfile.mkstemp()
        self.o = os.fdopen(fd, "w")
    def __del__(self):
        self.close()
    def close(self):
        if self.o is not None:
            self.o.close()
            self.o = None
            if (not os.path.isfile(self.path)) or (
                UpdatingFile.hashpath(self.tempfilepath) !=
                UpdatingFile.hashpath(self.path)):
                shutil.copyfile(self.tempfilepath, self.path)
                os.remove(self.tempfilepath)
                return True
            else:
                os.remove(self.tempfilepath)
                return False
    def write(self,*args, **kwargs):
        if self.o is not None:
            self.o.write(*args, **kwargs)
        else:
            sys.stderr.write(*args, **kwargs)


post_header = """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Voder-Vocoder: {title}</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body {{
font-family:sans-serif;
}}
.column {{
max-width:35em;
margin:0 auto;
}}
img {{
max-width:100%;
height:auto;
}}
</style>
</head>

  <!-- Copyright {year}-{current_year} Hal Canary. ALL RIGHTS RESERVED. -->

  <body>
    <div class="column">

      <h1 class="blogtitle">{title}</h1>
{summarypart}
      <div class="byline">By Hal Canary,
        {date}
        (<a href="{permalink}">link</a>)
{category_links}        </div>
      <!-- SRC= {src} -->
      <hr style="border:0;height:1px;color:#000;background-color:#000;">

      <div class="content">

<!-- BEGIN CONTENT -->
"""

class Post(object):
    @staticmethod
    def markdown(inf):
        subproc = subprocess.Popen(
            ['markdown'], stdin=subprocess.PIPE, stdout=subprocess.PIPE)
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
                elif key == 'PERMALINK':
                    self.permalink = value
                elif key == 'PERMALINKPART':
                    self.permalinkpart = value
                elif key == 'SUMMARY':
                    self.summary = value
                elif key == 'CATEGORIES':
                    self.categories = value
                elif key == 'MODE':
                    self.mode = value
                # else:
                #    self.values[key] = value

            if self.date is None:
                raise Exception('DATE missing ({})'.format(self.source_path))
            if self.postid is None:
                raise Exception('POSTID missing ({})'.format(self.source_path))
            if self.title is None:
                raise Exception('TITLE missing ({})'.format(self.source_path))

            if self.summary is not None:
                self.summarypart = (
                    '\n      <p class="byline">'
                    + self.summary + '</p>\n')
            else:
                self.summarypart = ''
            timestamp = datetime.datetime.strptime(
                self.date.split()[0],'%Y-%m-%d').date()
            self.year = '{:04d}'.format(timestamp.year)
            self.month = '{:02d}'.format(timestamp.month)
            self.day = '{:02d}'.format(timestamp.day)
            if self.permalinkpart is None:
                self.permalinkpart = '{}/{}/{}/{}/{}/'.format(
                    base_dir,self.year,self.month,self.day,
                    self.postid)
            if self.permalink is None:
                self.permalink = url_base + '/' + self.permalinkpart

            if self.permalinkpart[-1] == '/':
                self.target_file = self.permalinkpart + 'index.html'
            else:
                self.target_file = self.permalinkpart + '.html'

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
      <div style=";border-bottom: 6px solid #ddd; border-top: 6px solid #ddd; margin:0 -8px -6px; -8px; padding: 0 8px 0 8px">
      <h2 class="blogtitle">{title}</h2>
{summarypart}
      <div class="byline">By Hal Canary,
        {date}
        (<a href="{link}">link</a>)
{category_links}        </div>
      <!-- SRC= {src} -->
      <div class="content">
{post}
      </div>
      </div>

      <!-- ************************************************************** -->
"""
class RecentPosts(object):
    def __init__(self):
        self.loc = '{}/index.html'.format(base_dir)
        self.o = UpdatingFile(self.loc)
        self.o.write(top_posts_header.format(current_year=current_year))
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
        self.o = UpdatingFile(self.loc)
        self.o.write(archive_header.format(
                current_year=current_year,name=self.name))
    def close(self):
        self.o.write(archive_footer)
        if self.o.close():
            print 'modified', self.loc
    def addPost(self,post):
        relurlfixer = re.compile('^{}/(.*)$'.format(base_dir))
        short = relurlfixer.match(post.permalinkpart).groups()[0]
        relurl = self.upwards + short
        self.o.write(
            '<li>\n  <a href="{relurl}">{title}</a>\n  {date}\n'.format(
                relurl=relurl,title=post.title,date=post.date))
        if post.summary is not None:
            self.o.write('  <br>{}\n'.format(post.summary))
        self.o.write('</li>\n')

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
            findIndex(indices, ['archives'], '').addPost(post)
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
