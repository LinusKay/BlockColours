# BlockColours

A plugin that allows players to perform operations revolving around the colours of blocks.
This currently includes:
* Find blocks with similar colours
* Find blocks with complementary colours

All data for block colours and colour names is freely available to edit in both the blocks.yml and colours.yml files respectively. This allows you to tweak the colour values for specific blocks, and add/change the preset colour names.

## Commands

**/blockcolour similar [\<minDifference> \<maxDifference>] [\<minExclusion> \<maxExclusion>]** 

Find blocks with a similar colour base to the held block. The min/max Difference parameters allow you to search for blocks within a given difference range. The min/max exclusion parameters allow you to exclude a given range from the search.

\
![gif displaying get similar blocks command](https://media.giphy.com/media/K9IoW3iFt7SLxymwkg/giphy.gif)

\
![image displaying limited similar search](https://i.imgur.com/AF9r43q.png)
*/blockcolour similar 0 30* - Demonstration of a limited search, finding only blocks with a difference value between 0 and 30.


\
![image displaying limited similar search with exclusion](https://i.imgur.com/NnNP3T4.png)
*/blockcolour similar 0 30 24 27* - Demonstration of a limited search, finding only blocks with a difference value between 0 and 30, excluding all blocks with a difference value between 24 and 27.



---

\
**/blockcolour <complementary|complement|opposite> [\<minDifference> \<maxDifference>] [\<minExclusion> \<maxExclusion>]** 

Find blocks with a colour base opposing the held block. The min/max Difference parameters allow you to search for blocks within a given difference range. The min/max exclusion parameters allow you to exclude a given range from the search.
Rather than displaying blocks based upon how similar they are to the held block, blocks are displayed based upon how similar they are to the held block's opposite colour.

\
![gif displaying get complementary blocks command](https://media.giphy.com/media/ltM8VBKuRMNxF5uGC9/giphy.gif)

\
![image displaying limited complementary search](https://i.imgur.com/fM1UPxN.png)
*/blockcolour complementary 0 30* - Demonstration of a limited search, finding only blocks with a difference value between 0 and 100.

\
![image displaying limited complementary search](https://i.imgur.com/CJsH94Z.png)
*/blockcolour complementary 0 30 43 78* - Demonstration of a limited search, finding only blocks with a difference value between 0 and 100, excluding all blocks with a difference value between 43 and 78.

---

**/blockcolor gradient [\<startingSlot> \<endingSlot> | \<startingBlockName> \<endingBlockName>] [gradientSize]**

Generate a gradient of blocks between two given blocks. If no arguments given, will create gradient between off-hand and main hand blocks. Alternatively, player can specify the hotbar slots the two items they wish to use are in, or specify two blocks by name. The gradientSize parameter specifies how many blocks long the gradient should be. Gradients use a separate file, consisting of only full blocks.

\
![image displaying gradient example](https://i.imgur.com/rMrlobo.png)
*/blockcolour gradient 2 7 9* - Demonstration of a 9-block gradient, between blocks in slot 2 and 7.

\
![image displaying gradient example](https://i.imgur.com/QYwAtgC.png)
*/blockcolour gradient 15* - Demonstration of a 15-block gradient, between blocks in off-hand and main-hand.

\
![image displaying gradient example](https://i.imgur.com/gnRRVQq.png)
*/blockcolour gradient green_wool red_concrete 23* - Demonstration of a 23-block gradient, between green wool and red concrete.

---

\
To-Do
* Add pagination for block GUI
* Add blacklisting for specific blocks
* Add option to not display item lore
* Expand block colour data
* Consider conversion of calculations from RGB data to HSL to better handle shades (black/white)
