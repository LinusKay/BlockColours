# BlockColours

A plugin that allows players to perform operations revolving around the colours of blocks.
This currently includes:
* Find blocks with similar colours
* Find blocks with complementary colours
* Generate gradients of any size between two blocks
* Freely editable source data, meaning you can customise the colour mapping to your liking by adding, removing or editing colours.
* Blacklist unwanted blocks


## Commands
### Similar Blocks
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

### Complementary/Opposite Blocks
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

### Gradients
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
## Config

```yaml
# blacklist allows you to disallow certain blocks from appearing in any GUI
# 
blacklist:
  - STONE
  - BEDROCK
  - diamond_ore
  - yellow_concrete
  ```

---
## Editing Data
All of the source data used to map blocks to their respective colours is open for editing locally. This allows you to add support for additional blocks, remove unwanted blocks, or change the hexcode or colour name given to a block. In the current version, this data is all stored in YAML format (keep in mind that this may change in a later version).
To edit data, navigate to the 'blockcolours' folder, inside your server plugin folder, after having run the server at least once. There are a number of .yml files, each with a different purpose/scope. The plugin should have no issue handling data edits on the fly, however if you run into issues, try restarting the server to reload the config files.
* **blocks.yml** - contains colour mapping for most blocks
```yaml
acacia_door: "#c8744c"
acacia_leaves: "#8b8484"
acacia_log: "#6c645c"
...
```
* **blocks_full_only.yml** - contains colour mapping for full blocks only (complete cubes). This is used in gradient generation.
```yaml
acacia_leaves: "#8b8484"
acacia_log: "#6c645c"
acacia_planks: "#a15431"
...
```
* **colours.yml** - contains colour name data, used in matching each block's colour to it's respective name. Blocks are paired with the closest hexcode/name to their base colour.
```yaml
b0171f: "indian red"
dc143c: "crimson"
ffb6c1: "lightpink"
...
```
---
\
To-Do
* Add pagination for block GUI
* Expand block colour data
* Consider conversion of calculations from RGB data to HSL to better handle shades (black/white)
* Implement image colour parsing
