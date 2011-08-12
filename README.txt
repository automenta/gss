PURPOSE
----
The system that GSS attempts to model is one that:

 -Can calculate whether a complete manifest of needs can be satisfied by the ”available” resources -- illustrating how a community is likely to thrive or suffer.

 -Can suggest alternate means of satisfying a more essential need, when shortages occur; for example, by substituting an alternate source of a nutrient.

 -Can attempt to provide a means of evacuation, If needs are not likely to be met


Imagine a system that:

  -List and analyzes physical locations
  -Analyzes methods for transitioning from one location to another

...in terms of the appropriateness of that method for a biological organism
(according to certain measurement heuristics).

This tool should be easy and free for all people and animals to use (and modify; improve).


Appropriateness:

  -of a given environment,
  -at a specific time,
  -for a specific organism (human, animal, or plant),

...may be calculated in terms of its needs.

The development of this tool is equivalent to a planetary constitution that can logically grant all beings equal rights to existence.


TODO
----

Multithreaded getIntensity()

Adjust HeatMap's area scale
    --Currently it's fixed to 1.0
    --Fix displayed altitude/elevation -- try to use relative to ground, so when zoomed it is close to the ground point

Visualization of Combinations of Benefits/Threats
    --Simple (+benefit,-threat) determining the HeatMap's shape and color?

Implement "What May I Need?"

Add KML Support as subclass of DataPoints
    --display
    --interpreting a KML document's its identified items as Data

    --add some example DataSet's

Add Country-wide data as indicators at each of its major cities
    --needs dataset of N largest cities on Earth, N >> C (C = # of countries)
    --this will normalize datasets which are per-country by approximating a country's border shape by point sources of the major cities within its boundaries


