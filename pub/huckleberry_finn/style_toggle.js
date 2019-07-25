
class Toggle {
  #v;
  constructor() {
    var match = /;s=([^;]*);/.exec(";" + document.cookie + ";");
    this.#v = (match && match.length > 1) ? parseInt(match[1]) : 0;
  }
  get(bit) { return (this.#v & (1 << bit)) != 0; }
  toggle(bit) { document.cookie = "s=" + (this.#v ^= (1 << bit)); return this.get(bit); }
}

function set_color(state) {
  var s = document.body.style;
  s.backgroundColor = state ? "black" : "white";
  s.color           = state ? "white" : "black";
}

function set_font(state) {
  document.body.style.fontFamily = state ? "sans-serif" : "serif";
}

const COLOR_TOGGLE = 0;
const FONT_TOGGLE = 1;
