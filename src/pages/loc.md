# Get Current Location

<https://halcanary.org/vv/2013/08/29/3002/>

<noscript>Sorry, this page uses javascript.</noscript>

<script>
function nav() {
  if (! navigator.geolocation) {
    document.getElementById("location").innerHTML = "Sorry :(";
    return;
  }
  document.getElementById("location").innerHTML = "....";
  function ae(type, dst) {
    var x = document.createElement(type);
    dst.appendChild(x);
    return x;
  }
  function at(content, dst) {
    var x = document.createTextNode(content);
    dst.appendChild(x);
    return x;
  }
  function posfunc(pos) {
    var c = pos.coords;
    var r = 6;
    var p = c.latitude.toFixed(r) + "," + c.longitude.toFixed(r);
    var loc = document.getElementById("location");
    var url = 'https://maps.google.com/maps?q=' + p;
    loc.innerHTML = "";
    at('Your Current Location:', ae('strong', ae('p', loc)))
    var a = ae('a', ae('p', loc));
    a.href = url;
    at(p, a);
    at("("+ c.accuracy + "m)", ae('p', loc));
    a = ae('a', ae('p', loc));
    a.href = "geo:" + p;
    at("geo:" + p, a);
  };
  navigator.geolocation.getCurrentPosition(posfunc);
  return false;
}
window.addEventListener('load', nav);
</script>
<div id="location"></div>
