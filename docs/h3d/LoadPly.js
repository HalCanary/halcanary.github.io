import * as THREE from "./three.module.js";
import { OrbitControls } from './OrbitControls.js';
import { PLYLoader     } from './PLYLoader.js';

const LoadPly = (domRoot, url, width, height) => {
    var scene = new THREE.Scene();
    var loader = new PLYLoader();
    const material = new THREE.MeshPhongMaterial({
        vertexColors: THREE.VertexColors,
        flatShading: true,
    });
    loader.load(url,
                (v) => scene.add(new THREE.Mesh(v, material)),
                undefined,
                (e) => console.error(e));
    var camera = new THREE.PerspectiveCamera(
        75, width / height, 0.1, 1000 );
    camera.position.z = 100;
    var renderer = new THREE.WebGLRenderer();
    renderer.setSize( width, height );
    domRoot.appendChild(renderer.domElement);
    var controls = new OrbitControls( camera, renderer.domElement );
    var pointLight = new THREE.PointLight( 0xffffff, 0.5);
    pointLight.position.set(1,1,2);
    camera.add(pointLight);
    scene.add(camera);
    var light2 = new THREE.AmbientLight( 0x404040 ); // soft white light
    scene.add( light2 );
    function animate() {
        requestAnimationFrame( animate );
        renderer.render( scene, camera );
    }
    animate();
}
export default LoadPly;
