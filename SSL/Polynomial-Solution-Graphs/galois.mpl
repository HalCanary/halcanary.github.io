with(networks):

# encode an array as a single number.
encode := (x,F) -> ( F[output](x[1])*100 
	           + F[output](x[2])*10	
	           + F[output](x[3])*1 ): 

# Define the switch function.
switch := proc(a,b,c,F) 
       local x3,x1;
       x1 := F[input](1);
       x3 := F[`+`]( x1, F[`+`]( x1,x1 ) ) ;
       F[`-`]( F[`*`]( x3, F[`*`](a,b) ),c ); 
end:

# Define my three cluster functions.
f:= (x,F) -> [x[1],x[2],switch(x[1],x[2],x[3],F)]:
g:= (x,F) -> [x[1],switch(x[1],x[3],x[2],F),x[3]]:
h:= (x,F) -> [switch(x[2],x[3],x[1],F),x[2],x[3]]:
 
# Give me the graph neighbors of x.
N := x -> neighbors(x,G):
 
# Given a prime F, return a graph G(M,F)
makegraph := proc(F,firstelement)
   local g;
   g := graph({encode(firstelement,F)},{}):
   bleg(g, firstelement, 1, F):
   eval(g);
end:
 
# useful function
bleg := proc(G,x,depth,F)
  if depth < 100 then
    step(G,x,f(x,F),depth,F);
    step(G,x,g(x,F),depth,F);
    step(G,x,h(x,F),depth,F);
  fi;
end:
 
# useful function
step := proc(G,x,y,depth,F)
  if (encode(y,F) in vertices(G)) then
    addedge({encode(x,F),encode(y,F)},G):
  else
  #  if markoff(x,F) then
      addvertex(encode(y,F),G):
      addedge({encode(x,F),encode(y,F)},G):
      bleg(G,y,depth+1,F);
  #  fi
  fi
end:


# Usefull functions.
# is this a markoff solution?  For debuging
markoff := (x,F) -> markoff2(x[1],x[2],x[3],F):
markoff2 := proc(x,y,z,F)
    local lhs,rhs,x1,x3;
    x1 := F[input](1);
    x3 := F[`+`](x1,x1,x1) ;	
    lhs := F[`+`]( F[`^`](x,2), F[`^`](y,2), F[`^`](z,2));
    rhs := F[`*`]( x3, x,y,z);
    if lhs = rhs then true else false fi;
    #if (x^2 + y^2 + z^2 - 3*x*y*z) mod p = 0 then true else false fi;
end:

decode2 := proc(x)
    local n,one,ten,hun;
    n := 10;
    one := x mod n;
    ten := ((x-one)/n) mod n;
    hun := (x-one-(n*ten))/n^2;
    [hun,ten,one];
end:
decode := proc(x,F) 
    local y;
    y := decode2(x);
    [F[input](y[1]), F[input](y[2]), F[input](y[3])];
end:

# Enumerate ALL of the solutions
enumerate := proc(p,k,F)
	local a,b,c,A,B,C,sols;
	sols := {}:
	for a from 0 to p^k-1 do
	    for b from 0 to p^k-1 do
		for c from 0 to p^k-1 do
		    A := F[input](a):
		    B := F[input](b):
		    C := F[input](c):
		    if markoff([A,B,C],F) then 
			sols := sols union {[A,B,C]};
		    fi
		od    
	    od
	od:  
	sols;
end:


# here is where things begin to happen.
#let our field F be
p:=2:
k:=2:
F := GF(p,k):
#seq(F[input](x),x=0..p^k-1);
#G := makegraph(F,[F[input](1),F[input](1),F[input](1)]):
#nops(vertices(G));
#vertices(G);
#draw3d(G);
#draw(G);
sols := enumerate(p,k,F):
nops(sols);
sols;


#found solutions.
u := {}:
# set of connected components.
Gees := {}:
for x in sols do 
    if not(x in u)  then
        G := makegraph(F, x);
        for y in vertices(G) do
            u := u union {decode(y,F)}; 
        od:
        Gees := Gees union {eval(G)}; 
    fi
od:
nops(Gees);
draw3d(Gees[1]);
seq(nops(vertices(Gees[i])), i = 1..nops(Gees));
#seq((vertices(Gees[i])), i = 1..nops(Gees));



