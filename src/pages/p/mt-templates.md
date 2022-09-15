Movabletype templates for halcanary.org
=======================================

All seven templates on this page are duel licensed under the [Creative Commons Share Alike License](http://creativecommons.org/licenses/sa/1.0/) and the [GNU General Public Licence](http://www.gnu.org/licenses/gpl.txt). You may use and redistribute this code under the terms of either of these liscences. All templates partially derived from MT's defaults.

Here are the four movable type templates for archive-type summaries. They are all included into a php file, using <?php include "filename" ?>.

mt1 makes my front page, with the last 10 entries.

<table class="code"><tbody><tr><th>mt1.template.html</th></tr><tr><td><pre><code>
    &lt;!--(c) 2003 Hal Canary--&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;Log&lt;/h2&gt;

      &lt;div class="blog"&gt;
      &lt;p class="centered"&gt;(&lt;a href="./archives/"&gt;Archives&lt;/a&gt;)&lt;/p&gt;
      &lt;/div&gt;
      &lt;MTEntries lastn="10"&gt;

      &lt;div class="blog"&gt;
	&lt;h3 class="blogtitle"&gt;&lt;$MTEntryTitle$&gt;&lt;/h3&gt;

	&lt;$MTEntryBody$&gt;	

        &lt;MTEntryIfExtended&gt;
        &lt;p class="blog-notes"&gt;
	  &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;
	    Continue reading &lt;em&gt;&lt;$MTEntryTitle$&gt;&lt;/em&gt;
	  &lt;/a&gt;
	&lt;/p&gt;
        &lt;/MTEntryIfExtended&gt;

	&lt;p class="blog-notes"&gt;
	  &lt;a href="&lt;$MTEntryAuthorURL$&gt;"&gt;&lt;$MTEntryAuthor$&gt;&lt;/a&gt; |
	  &lt;$MTEntryCategory$&gt; |
	  &lt;$MTEntryDate format="%Y-%m-%d, %I:%M %p"$&gt; |
	  &lt;!--&lt;a href="&lt;$MTEntryLink$&gt;"&gt;permlink &amp;amp; comments&lt;/a&gt;--&gt;
	  &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;permlink &amp;amp; comments&lt;/a&gt;
	  (&lt;$MTEntryCommentCount$&gt;)
	&lt;/p&gt;
      &lt;/div&gt;

      &lt;hr class='blogsep' /&gt;

      &lt;/MTEntries&gt;

    &lt;/div&gt;
</code></pre></td></tr></tbody></table>

mt2 also goes on the front page, with links to the last 20 entries.

<table class="code"><tbody><tr><th>mt2.template.html</th></tr><tr><td><pre><code>
    &lt;!--(c) 2003 Hal Canary--&gt;
    &lt;div class="section"&gt;

      &lt;h2&gt;Recent&lt;/h2&gt;

      &lt;MTEntries lastn="30"&gt;
      &lt;p&gt;
        &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;&lt;$MTEntryTitle$&gt;&lt;/a&gt;
	&lt;MTComments lastn="1"&gt;
	(&lt;$MTEntryCommentCount$&gt;)
	&lt;/MTComments&gt;
	&lt;br /&gt;
	&lt;$MTArchiveDate format="%Y-%m-%d, %I:%M %p"$&gt;
      &lt;/p&gt;
      &lt;/MTEntries&gt;
    &lt;/div&gt;
</code></pre></td></tr></tbody></table>

mt3 is the full archive, ordered by date

<table class="code"><tbody><tr><th>mt3.template.html</th></tr><tr><td><pre><code>
    &lt;!--(c) 2003 Hal Canary--&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;Archive&lt;/h2&gt;

      &lt;div class='subsection'&gt;
        &lt;p&gt;&lt;a href="../"&gt;home&lt;/a&gt; | 
	&lt;a href="./cat.php"&gt;category archive&lt;/a&gt;&lt;/p&gt;
      &lt;/div&gt;

      &lt;MTArchiveList archive_type="Monthly"&gt;
      &lt;div class='subsection'&gt;
	&lt;h3&gt;
	&lt;a href="&lt;$MTArchiveLink$&gt;"&gt;
	&lt;$MTArchiveTitle$&gt;&lt;/a&gt;
	  (&lt;$MTArchiveCount$&gt;)&lt;/h3&gt;
	&lt;ul&gt;
	  &lt;MTEntries&gt;
	  &lt;li&gt;
            &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;
	    &lt;$MTEntryTitle$&gt;&lt;/a&gt; (&lt;$MTEntryCategory$&gt;) 
	    &lt;$MTArchiveDate format="%Y-%m-%d, %I:%M %p"$&gt; 
	    &lt;MTComments lastn="1"&gt;
	    (&lt;$MTEntryCommentCount$&gt;)&lt;/MTComments&gt;
	  &lt;/li&gt;
	  &lt;/MTEntries&gt;
	&lt;/ul&gt;
      &lt;/div&gt;
      &lt;/MTArchiveList&gt;
    &lt;/div&gt;
</code></pre></td></tr></tbody></table>

mt4 is the full archive, ordered by category

<table class="code"><tbody><tr><th>mt4.template.html</th></tr><tr><td><pre><code>
    &lt;!--(c) 2003 Hal Canary--&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;Category Archive&lt;/h2&gt;
      &lt;MTCategories&gt;
      &lt;div class='subsection'&gt;
        &lt;h3&gt;&lt;a href="&lt;$MTCategoryArchiveLink$&gt;"&gt;
	&lt;$MTCategoryLabel$&gt;&lt;/a&gt;
        (&lt;$MTCategoryCount$&gt;)&lt;/h3&gt;
	&lt;ul&gt;
	  &lt;MTEntries&gt;
	  &lt;li&gt;
            &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;
	    &lt;$MTEntryTitle$&gt;&lt;/a&gt; 
	    &lt;$MTArchiveDate format="%Y-%m-%d, %I:%M %p"$&gt; 
	    &lt;MTComments lastn="1"&gt;
	    (&lt;$MTEntryCommentCount$&gt;)
	    &lt;/MTComments&gt;
	  &lt;/li&gt;
	  &lt;/MTEntries&gt;
	&lt;/ul&gt;
      &lt;/div&gt;
      &lt;/MTCategories&gt;
    &lt;/div&gt;
</code></pre></td></tr></tbody></table>

The next three compile into complete files in the /archive/ directory.

<table class="code"><tbody><tr><th>individual-entry-archive.html</th></tr><tr><td><pre><code>
&lt;$MTInclude file="head.html"$&gt;
&lt;!--(c) 2003 Hal Canary--&gt;
&lt;body&gt;
  &lt;div class="banner"&gt;
  &lt;h1&gt;
    &lt;img src="/images/new-hal-icon.jpg" alt="" /&gt;
    WoHC: &lt;$MTEntryTitle$&gt;
  &lt;/h1&gt;
  &lt;/div&gt;
  &lt;div class='widerightcol'&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;Log&lt;/h2&gt;

      &lt;p&gt;
        &lt;MTEntryPrevious&gt;
        &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;&amp;laquo; &lt;$MTEntryTitle$&gt;&lt;/a&gt; |
	&lt;/MTEntryPrevious&gt;
	&lt;a href="&lt;$MTBlogURL$&gt;"&gt;Home&lt;/a&gt;
	| &lt;a href="./"&gt;Archives&lt;/a&gt;
	| &lt;a href="&lt;$MTEntryLink archive_type="Monthly"$&gt;"&gt;Month&lt;/a&gt;
	&lt;MTEntryNext&gt;
	| &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;&lt;$MTEntryTitle$&gt; &amp;raquo;&lt;/a&gt;
	&lt;/MTEntryNext&gt;
      &lt;/p&gt;

      &lt;div class="blog"&gt;
	&lt;h3 class="blogtitle"&gt;&lt;$MTEntryTitle$&gt;&lt;/h3&gt;
	&lt;$MTEntryBody$&gt;	
	&lt;$MTEntryMore$&gt;
	&lt;p class="blog-notes"&gt;
	  &lt;a href="&lt;$MTEntryAuthorURL$&gt;"&gt;&lt;$MTEntryAuthor$&gt;&lt;/a&gt; | 
	  &lt;$MTEntryCategory$&gt; |
	  &lt;$MTEntryDate format="%Y-%m-%d, %I:%M %p"$&gt;
	&lt;/p&gt;
      &lt;/div&gt;
    &lt;/div&gt;

    &lt;MTEntryIfAllowComments&gt;

    &lt;MTComments lastn="1"&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;Comments&lt;/h2&gt;
    &lt;/MTComments&gt;

      &lt;MTComments&gt;
      &lt;div class="blog"&gt;
        &lt;p&gt;
	  &lt;$MTCommentBody$&gt;
	&lt;/p&gt;
	&lt;p class="blog-notes"&gt;
	  Posted by &lt;$MTCommentAuthorLink spam_protect="1"$&gt; 
	  at &lt;$MTCommentDate format="%Y-%m-%d, %I:%M %p"$&gt;
	&lt;/p&gt;
      &lt;/div&gt;
      &lt;/MTComments&gt;

    &lt;MTComments lastn="1"&gt;
    &lt;/div&gt;
    &lt;/MTComments&gt;

    &lt;MTEntryIfCommentsOpen&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;Comment!&lt;/h2&gt;
      
      &lt;div class='subsection'&gt;
      &lt;form method="post" 
            action="&lt;$MTCGIPath$&gt;&lt;$MTCommentScript$&gt;" 
	    name="comments_form" 
	    onsubmit="if (this.bakecookie[0].checked) rememberMe(this)"&gt;
	&lt;input type="hidden" name="static" value="1" /&gt;
	&lt;input type="hidden" name="entry_id" value="&lt;$MTEntryID$&gt;" /&gt;

	&lt;p&gt;
	   &lt;label for="author"&gt;Name:&lt;/label&gt;
	   &lt;br /&gt;
	   &lt;input tabindex="1" id="author" name="author" /&gt;
	&lt;/p&gt;
	&lt;p&gt;
	  &lt;label for="email"&gt;Email Address:&lt;/label&gt;
	  &lt;br /&gt;
	  &lt;input tabindex="2" id="email" name="email" /&gt;
	&lt;/p&gt;
	&lt;p&gt;
	  &lt;label for="url"&gt;URL:&lt;/label&gt;
	  &lt;br /&gt;
	  &lt;input tabindex="3" id="url" name="url" /&gt;
	&lt;/p&gt;
	
	&lt;p&gt;
	  Remember personal info?
	  &lt;br /&gt;
	  &lt;input type="radio" id="bakecookie" name="bakecookie" /&gt;
	  &lt;label for="bakecookie"&gt;Yes&lt;/label&gt;
	  &lt;input type="radio" id="forget" name="bakecookie" 
	         onclick="forgetMe(this.form)" value="Forget Info" 
		 style="margin-left: 15px;" /&gt;
	  &lt;label for="forget"&gt;No&lt;/label&gt;
	  &lt;br style="clear: both;" /&gt;
	&lt;/p&gt;
	&lt;p&gt;
	  &lt;label for="text"&gt;Comments:&lt;/label&gt;
	  &lt;br /&gt;
	  &lt;textarea tabindex="4" id="text" name="text" 
	            rows="10" cols="50"&gt;&lt;/textarea&gt;
	&lt;/p&gt;
	&lt;p&gt;
	  &lt;input type="submit" name="preview" value="&amp;nbsp;Preview&amp;nbsp;" /&gt;
	  &lt;input style="font-weight: bold;" type="submit" 
	         name="post" value="&amp;nbsp;Post&amp;nbsp;" /&gt;
	&lt;/p&gt;
      &lt;/form&gt;
      &lt;/div&gt;

      &lt;script type="text/javascript" language="javascript"&gt;
        &lt;!--
	  document.comments_form.email.value = getCookie("mtcmtmail");
	  document.comments_form.author.value = getCookie("mtcmtauth");
	  document.comments_form.url.value = getCookie("mtcmthome");
	  if (getCookie("mtcmtauth")) {
	      document.comments_form.bakecookie[0].checked = true;
	  } else {
	      document.comments_form.bakecookie[1].checked = true;
	  } // 
	--&gt;
      &lt;/script&gt;
    &lt;/div&gt;
    &lt;/MTEntryIfCommentsOpen&gt;
    &lt;/MTEntryIfAllowComments&gt;

    &lt;$MTInclude file="bottom.html"$&gt;
    &lt;hr class='middle' /&gt;
  &lt;/div&gt;

  &lt;div class='leftcol'&gt;
    &lt;$MTInclude file="leftcol.html"$&gt;  
  &lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
</code></pre></td></tr></tbody></table>

<table class="code"><tbody><tr><th>date-based-archive.html</th></tr><tr><td><pre><code>
&lt;$MTInclude file="head.html"$&gt;
&lt;!--(c) 2003 Hal Canary--&gt;
&lt;body&gt;
  &lt;div class="banner"&gt;
  &lt;h1&gt;
    &lt;img src="/images/new-hal-icon.jpg" alt="" /&gt;
    WoHC Log
  &lt;/h1&gt;
  &lt;/div&gt;
  &lt;div class='widerightcol'&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;&lt;$MTArchiveTitle$&gt;&lt;/h2&gt;
      &lt;div class="subsection"&gt;
        &lt;p&gt;
          &lt;MTArchivePrevious&gt;
	  &lt;a href="&lt;$MTArchiveLink$&gt;"&gt;&amp;laquo; &lt;$MTArchiveTitle$&gt;&lt;/a&gt; |
	  &lt;/MTArchivePrevious&gt;
	  &lt;a href="&lt;$MTBlogURL$&gt;"&gt;Main&lt;/a&gt; | 
	  &lt;a href="./"&gt;Archives&lt;/a&gt;
	  &lt;MTArchiveNext&gt;
	  | &lt;a href="&lt;$MTArchiveLink$&gt;"&gt;&lt;$MTArchiveTitle$&gt; &amp;raquo;&lt;/a&gt;
	  &lt;/MTArchiveNext&gt;
	&lt;/p&gt;
      &lt;/div&gt;

      &lt;MTEntries sort_order="ascend"&gt;
      &lt;div class="blog"&gt;
	&lt;h3 class="blogtitle"&gt;&lt;$MTEntryTitle$&gt;&lt;/h3&gt;
	&lt;$MTEntryBody$&gt;	
	&lt;$MTEntryMore$&gt;
	&lt;p class="blog-notes"&gt;
	  &lt;a href="&lt;$MTEntryAuthorURL$&gt;"&gt;&lt;$MTEntryAuthor$&gt;&lt;/a&gt; | 
	  &lt;$MTEntryCategory$&gt; |
	  &lt;$MTEntryDate format="%Y-%m-%d, %I:%M %p"$&gt; |
	  &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;permlink &amp;amp; comments&lt;/a&gt;
	  (&lt;$MTEntryCommentCount$&gt;)
	&lt;/p&gt;
      &lt;/div&gt;
      &lt;/MTEntries&gt;

    &lt;/div&gt;


    &lt;$MTInclude file="bottom.html"$&gt;
    &lt;hr class='middle' /&gt;
  &lt;/div&gt;

  &lt;div class='leftcol'&gt;
    &lt;$MTInclude file="leftcol.html"$&gt;  
  &lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
</code></pre></td></tr></tbody></table>

<table class="code"><tbody><tr><th>category-archive.html</th></tr><tr><td><pre><code>
&lt;$MTInclude file="head.html"$&gt;
&lt;!--(c) 2003 Hal Canary--&gt;
&lt;body&gt;
  &lt;h1&gt;
    &lt;img src="/images/new-hal-icon.jpg" alt="" /&gt;
    WoHC Log
  &lt;/h1&gt;
  &lt;div class='widerightcol'&gt;
    &lt;div class='section'&gt;
      &lt;h2&gt;Category: &lt;$MTArchiveTitle$&gt;&lt;/h2&gt;
      &lt;div class="subsection"&gt;
        &lt;p&gt;
	  &lt;a href="&lt;$MTBlogURL$&gt;"&gt;Main&lt;/a&gt; | 
	  &lt;a href="./"&gt;Archives&lt;/a&gt; |
	  &lt;a href="./cat.php"&gt;Categories&lt;/a&gt;
	&lt;/p&gt;
      &lt;/div&gt;

      &lt;MTEntries sort_order="ascend"&gt;
      &lt;div class="blog"&gt;
	&lt;h3 class="blogtitle"&gt;&lt;$MTEntryTitle$&gt;&lt;/h3&gt;
	&lt;$MTEntryBody$&gt;	
	&lt;$MTEntryMore$&gt;
	&lt;p class="blog-notes"&gt;
	  &lt;a href="&lt;$MTEntryAuthorURL$&gt;"&gt;&lt;$MTEntryAuthor$&gt;&lt;/a&gt; | 
	  &lt;$MTEntryDate format="%Y-%m-%d, %I:%M %p"$&gt; |
	  &lt;a href="&lt;$MTEntryPermalink$&gt;"&gt;permlink &amp;amp; comments&lt;/a&gt;
	  (&lt;$MTEntryCommentCount$&gt;)
	&lt;/p&gt;
      &lt;/div&gt;
      &lt;/MTEntries&gt;

    &lt;/div&gt;
    &lt;$MTInclude file="bottom.html"$&gt;
    &lt;hr class='middle' /&gt;
  &lt;/div&gt;

  &lt;div class='leftcol'&gt;
    &lt;$MTInclude file="leftcol.html"$&gt;  
  &lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
</code></pre></td></tr></tbody></table>

* * *

<div class="rightside"><em>file modification time: 2003-12-17 23:18:50</em></div>

