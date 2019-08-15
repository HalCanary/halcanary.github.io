
class Toggle {
  #v;
  constructor() {
  var match = /;s=([^;]*);/.exec(";" + document.cookie + ";");
  this.#v = (match && match.length > 1) ? parseInt(match[1]) : 0;
  }
  get(bit) { return (this.#v & (1 << bit)) != 0; }
  toggle(bit) { document.cookie = "s=" + (this.#v ^= (1 << bit)); return this.get(bit); }
}

function init_toggle() {
  function set_color(state) {
    var s = document.body.style;
    s.backgroundColor = state ? "black" : "white";
    s.color       = state ? "white" : "black";
  }
  function set_font(state) {
    document.body.style.fontFamily = state ? "sans-serif" : "serif";
  }
  const COLOR_TOGGLE = 0;
  const FONT_TOGGLE = 1;
  window.toggle = new Toggle();
  set_color(window.toggle.get(COLOR_TOGGLE));
  set_font(window.toggle.get(FONT_TOGGLE));
  window.addEventListener("load", function() {
    var p = document.createElement("p");
    var b = document.createElement("button");
    b.appendChild(document.createTextNode("toggle night mode"));
    b.onclick = function() { set_color(window.toggle.toggle(COLOR_TOGGLE)); };
    p.appendChild(b);
    b = document.createElement("button");
    b.appendChild(document.createTextNode("toggle font"));
    b.onclick = function() { set_font(window.toggle.toggle(FONT_TOGGLE)); };
    p.appendChild(b);
    document.body.insertBefore(p, document.body.firstChild);
  });
}

function init_chapter() {
  function change_chapter() {
    var x = document.getElementById(location.hash.substring(1));
    if (x && x != window.chapter) {
      x.style.display = 'block';
      if (window.chapter) {
        window.chapter.style.display = 'none';
      }
      window.chapter = x;
    }
  }
  window.chapter = null;
  window.addEventListener("hashchange", change_chapter);
  window.addEventListener("load", change_chapter);
}
