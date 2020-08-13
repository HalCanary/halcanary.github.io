Hal Canary / Portfolio
======================

[Home](/)

* * *


<div id="Visualizing_Likelihood_Density_Functions_via_Optimal_Region_Projection"></div>

## Visualizing Likelihood Density Functions via Optimal Region Projection

<img src="/images/Optimal_Region_Projection_-_Gomez.png" alt="" width="640" height="659">

Hal Canary, Russell M. Taylor II, Cory Quammen, Scott Pratt, Facundo A. Gómez, Brian O'Shea, Christopher G. Healey. “[Visualizing Likelihood Density Functions via Optimal Region Projection](http://www.sciencedirect.com/science/article/pii/S0097849314000296).” _Computers & Graphics_ 41 (2014): 62-71.

_Abstract_: Effective visualization of high-likelihood regions of parameter space is severely hampered by the large number of parameter dimensions that many models have. We present a novel technique, Optimal Percentile Region Projection, to visualize a high-dimensional likelihood density function that enables the viewer to understand the shape of the high-likelihood region. Optimal Percentile Region Projection has three novel components: first, we select the region of high likelihood in the high-dimensional space before projecting its shadow into a lower-dimensional projected space. Second, we analyze features on the surface of the region in the projected space to select the projection direction that shows the most interesting parameter dependencies. Finally, we use a three-dimensional projection space to show features that are not salient in only two dimensions. The viewer can also choose sets of axes to project along to explore subsets of the parameter space, using either the original parameter axes or principal-component axes. The technique was evaluated by our domain-science collaborators, who found it to be superior to their existing workflow both when there were interesting dependencies between parameters and when there were not.

([Full Paper](/optimal-region-projection))

* * *

<div id="Percentile_Surface_Filter"></div>

## Percentile Surface Filter

<img src="/images/Percentile_Surface_Filter.png" alt="" width="1024" height="597">

Given a set of points in space with a scalar value, this filter will create a surface around the top _P_ percentile of the points. In this example, the top 95% of the points (by logLikelihood) are used. This VTK filter is included in the [MADAI Visualization Workbench](http://vis.madai.us/)

* * *

<div id="Matplotlib_Scatterplot_Matrix_Script"></div>

## Matplotlib Scatterplot Matrix Script

<img src="/images/Matplotlib_Scatterplot_Matrix_Script.png" alt="" width="768" height="768">

One standard way to visualize a multi-dimensional dataset is the scatterplot matrix. The datasets produced by Markov-chain Monte Carlo explorations of 3- to 20-dimensional space have millions of points — too many for a glyph at each point. [This script](http://cs.unc.edu/~hal/pub/madai_matplotlib_scatterplot_matrix) displays the density at each projected point. The upper-right side plots the log of the density to highlight low-density regions.

* * *

<div id="VTK_Viewer"></div>

## VTK Viewer

<img src="/images/VTK_Viewer.png" alt="" width="751" height="539">

My VTK Viewer VTK/Qt/C++ program lets you quickly view several 3D file formats (VTK, PLY, OBJ, STL, or PDB). I also have a VTK/Python version of the same program. It can be found at [https://github.com/HalCanary/vtkviewer](https://github.com/HalCanary/vtkviewer).

* * *

<div id="Word_Count_Journal"></div>

## Word Count Journal

<img src="/images/Word_Count_Journal.png" alt="" width="684" height="204">

A Python/Qt single-file text editor that counts words. I use it to write a journal entry each morning. It can be found at [https://github.com/HalCanary/WordCountJournal](https://github.com/HalCanary/WordCountJournal).

* * *

<div id="Teardrop_Glyphs_as_3D_Arrows"></div>

## Teardrop Glyphs as 3D Arrows

<img src="/images/Teardrop_Glyphs_as_3D_Arrows.png" alt="" width="1576" height="788">

Arrow Glyphs introduce a lot of visual clutter, especially when there are many of them. The teardrops glyph is the 3D version of the "anisotropic splat." Here is one frame from a movie depicting particles in a high-energy collision; glyph direction and size represent momentum. [teardrop.obj](http://cs.unc.edu/~hal/pub/teardrop.obj)

* * *

<div id="Banded_Level_Colormap_ParaView_Macro"></div>

## Banded Level Colormap ParaView Macro

<img src="/images/Banded_Level_Colormap_ParaView_Macro.png" alt="" width="1370" height="903">

My Banded\_Level\_Colormap ParaView macro was inspired by Van Wijk and Telea's Enridged Contour Maps. This macro is included in the [MADAI Visualization Workbench](http://vis.madai.us/).

* * *

<div id="3D_Frame_ParaView_Macro"></div>

## 3D Frame ParaView Macro

<img src="/images/3D_Frame_ParaView_Macro.png" alt="" width="788" height="575">

Drawing a frame around objects in space can serve to add context to improve perception. We know that the visual system is tuned to perceive shaded surfaces. Therefore, a frame built from polygons will help the most with the perception of depth. I wrote a [ParaView macro that generates a frame based on the bounding box of the currently active dataset](http://cs.unc.edu/~hal/pub/Generate_Frame.py).

* * *

<div id="Hephaestus_Scanner"></div>

## Hephaestus Scanner

<img src="/images/Hephaestus_Scanner.png" alt="" width="860" height="574">

For a class project, I wrote a content acquisition application that makes use of the Microsoft Kinect and the Point Cloud Library to construct a digital representation of a scene in VTK format and uploads it to a MIDAS backend server that stores and manages the collection of scenes from users and organizations. This project can be found at [github.com/HephaestusVision/](https://github.com/HephaestusVision/hephaestus/).

* * *

<div id="Visualizing_the_QCD_Critical_Point"></div>

## Visualizing the QCD Critical Point with the Gaussian Scalar Splatter

<img src="/images/Visualizing_the_QCD_Critical_Point_with_the_Gaussian_Scalar_Splatter.png" alt="" width="2400" height="2400">

My Gaussian Scalar Spatter Filter was used to visualize the critical point in the phase space of QCD matter in this paper.

_Caption_: Time evolution (top to bottom) of QCD matter created in central Au+Au collisions at 10 (left), 25 (center) and 40 (right) GeV/u beam energy projected into the T − μq phase diagram. The gray shading represents the amount of matter present at the respective value of T and μq.

Steffen A. Bass, Hannah Petersen, Cory Quammen, Hal Canary, Christopher G. Healey, Russell M. Taylor II. "[Probing the QCD Critical Point with Relativistic Heavy-Ion Collisions](http://dx.doi.org/10.2478/s11534-012-0076-1)." Central European Journal of Physics (2012) 10, 1278-1281.

My Gaussian Scalar Spatter Filter is included in the [MADAI Visualization Workbench](http://vis.madai.us/).

* * *

<div id="2D_Gaussian_Spatter"></div>

## 2D Gaussian Spatter

<img src="/images/2D_Gaussian_Spatter.png" alt="" width="1344" height="576">

This is a comparison of two different ways to visualize a set of points in two dimensions. By warping a surface by splatted density, it is easier to perceive relative densities.

My Gaussian Scalar Spatter Filter is included in the [MADAI Visualization Workbench](http://vis.madai.us/).

* * *

<div id="Visualizing_Significant_Differences"></div>

## Visualizing Significant Differences

<img src="/images/Visualizing_Significant_Differences.jpg" alt="" width="3840" height="2160">

This shows the differences between two hydrodynamic simulations of gold-nucleus collisions developed by Hannah Petersen and Steffen Bass. The side images each depict energy levels within a single 3D simulation, with the remnants of two nuclei flying towards the top and bottom of the screen, leaving a quark-gluon plasma in between. Wedges of the volume have been carved away, like removing a wedge from an onion to reveal internal structure. The center image shows two surfaces, one where the energy from the left simulation is higher, and one where the energy from the right simulation is greater. It also shows one surface from the left-hand simulation as a wire-frame mesh for context. Our collaborators want to understand the impact of changing simulation parameters on the resulting time-varying simulation. These techniques, which have been implemented as plug-ins for the open-source ParaView visualization application, are used by our collaborators studying particle collisions and weather simulation to make their simulations better match experimental results and to improve comprehension of the underlying physics.

These two techniques (onion slices on the side images, wireframe/contours on the central) were developed specifically for this type of data analysis in the spring of 2011 in a course on scientific visualization at UNC. This particular combination of occlusion and wire-frame transparency highlights the answers to the questions posed by the scientists in fresh ways that enable them to more easily gain insight into their science.

* * *

<div id="Ensemble_Surface_Slicing"></div>

## Ensemble Surface Slicing

<img src="/images/Ensemble_Surface_Slicing.png" alt="" width="804" height="630">

This is an implementation of Ensemble Surface Slicing, a [technique developed by Oluwafemi Alabi](http://dx.doi.org/10.1117/12.908288).

Ensemble\_Surface\_Slicing.py is a VTK+Python+QT program that enables a user to load several VTK surface files ﬁles and adjust the width of the slices, the direction of the slice plane, and the offset from the origin of the first slice. For COMP770 (Computer Graphics), I wrote up a [report detailing the development of this software](http://cs.unc.edu/~hal/class/comp770/ess_report.pdf).

This functionality has been incorporated into the [MADAI Visualization Workbench](http://vis.madai.us/).

* * *

<div id="Ray_Tracer"></div>

## Ray Tracer

<img src="/images/Ray_Tracer.png" alt="" width="1048" height="788">

I implemented a ray tracer for COMP770 (Computer Graphics).

* * *

<div id="ARPS_Weather_Visualization"></div>

## ARPS Weather Visualization

<img src="/images/ARPS_Weather_Visualization.png" alt="" width="1483" height="808">

In 2011, I worked with meteorologists to develop tools for visualizing weather simulation data. This is an example of [ARPS](http://wwwcaps.ou.edu/) data visualized in ParaView.

* * *

<div id="WRF_Weather_Visualization"></div>

## WRF Weather Visualization

<img src="/images/WRF_Weather_Visualization.png" alt="" width="1370" height="903">

In 2011, I worked with meteorologists to develop tools for visualizing weather simulation data. This is an example of [WRF](http://www.wrf-model.org/index.php) data visualized in ParaView.

* * *

<div id="Aztec_Diamonds_and_Baxter_Permutations"></div>

## Aztec Diamonds and Baxter Permutations

<img src="/images/Aztec_Diamonds_and_Baxter_Permutations.png" alt="" width="726" height="245">

Hal Canary. "[Aztec Diamonds and Baxter Permutations](http://www.combinatorics.org/Volume_17/Abstracts/v17i1r105.html)". _The Electronic Journal of Combinatorics_ **17** (2010), #R105

_Abstract_: We present a proof of a conjecture about the relationship between Baxter permutations and pairs of alternating sign matrices that are produced from domino tilings of Aztec diamonds. It is shown that a tiling corresponds to a pair of ASMs that are both permutation matrices if and only if the larger permutation matrix corresponds to a Baxter permutation. There has been a thriving literature on both pattern-avoiding permutations of various kinds \[Baxter 1964, Dulucq and Guibert 1988\] and tilings of regions using dominoes or rhombuses as tiles \[Elkies et al. 1992, Kuo 2004\]. However, there have not as of yet been many links between these two areas of enumerative combinatorics. This paper gives one such link.

* * *

