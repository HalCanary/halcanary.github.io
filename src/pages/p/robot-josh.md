Robot Josh
==========

07/21/2004 05:39:25 PM

<script type="text/javascript">function askRobotJosh() { var ques = document.getElementById('q1').value; if (ques == "") { return true; } document.getElementById('q1').value = ""; var p = document.getElementById('p1'); var s = document.createElement("span"); s.setAttribute("style","color:#16569e"); s.appendChild(document.createTextNode("You: ")); p.appendChild(s) p.appendChild(document.createTextNode(ques)); p.appendChild(document.createElement("br")); window.setTimeout("joshAnswers()",500); } function joshAnswers() { var p = document.getElementById('p1'); var phrase; var randy = Math.floor(6*Math.random()) if (randy == 0) { phrase = "Put that anywhere!"; } else if (randy == 1) { phrase = "JEsus!"; } else if (randy == 2) { phrase = "I said no!"; } else if (randy == 3) { phrase = "I havent's been drinking!"; } else if (randy == 4) { phrase = "A little bit, yes."; } else { phrase = "If you think it will help?"; } var s = document.createElement("span"); s.setAttribute("style","color:#a82f2f"); s.appendChild(document.createTextNode("Robot Josh: ")); p.appendChild(s) p.appendChild(document.createTextNode(phrase)); p.appendChild(document.createElement("br")); }</script>

<form onsubmit="askRobotJosh();return false;" action=""><p>Talk to Robot Josh: <input type="text" id="q1" value="Hello." size="40"> <input type="submit" value="Submit!"></p></form>

* * *

file modification time: 2006-09-14 20:16:30

* * *
