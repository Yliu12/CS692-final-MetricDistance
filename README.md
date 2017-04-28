# CS692-final-MetricDistance #

Calculate Metrics Distance.

### Sample input ###

example.txt

7,5
Mod11,1,2,3,4,5

Mod12,1,2,3,4,5

Mod2,1,3,3,4,5

Mod4,1,2,4,4,5

Mod5,2,2,3,4,5

Mod6,1,3,5,6,7

Mod7,1,3,4,4,5

### Sample output ###

digraph G {

Mod7->Mod2[label="m3 (1)"];

Mod11Mod12->Mod2[label="m2 (1)"];

Mod6->Mod7[label="m3 (1),m4 (2),m5 (2)"];

Mod4->Mod7[label="m2 (1)"];

Mod2->Mod7[label="m3 (1)"];

Mod5->Mod11Mod12[label="m1 (1)"];

}

The diagraph can be generated using
[graphviz](http://www.webgraphviz.com/)

