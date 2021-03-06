---
title: EscapedCharacter Extension Spec
author: 
version: 
date: '2016-06-06'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## EscapedCharacter  

Converts escaped_character text to EscapedCharacter nodes.

```````````````````````````````` example EscapedCharacter: 1
Sample  \t\e\x\t and \\ \- \~
.
<p>Sample  \t\e\x\t and \ - ~</p>
.
Document[0, 30]
  Paragraph[0, 30]
    TextBase[0, 29] chars:[0, 29, "Sampl … \- \~"]
      Text[0, 21] chars:[0, 21, "Sampl …  and "]
      EscapedCharacter[21, 23] textOpen:[21, 22, "\"] text:[22, 23, "\"]
      Text[23, 24] chars:[23, 24, " "]
      EscapedCharacter[24, 26] textOpen:[24, 25, "\"] text:[25, 26, "-"]
      Text[26, 27] chars:[26, 27, " "]
      EscapedCharacter[27, 29] textOpen:[27, 28, "\"] text:[28, 29, "~"]
````````````````````````````````


## Source Position Attribute

```````````````````````````````` example(Source Position Attribute: 1) options(src-pos)
Sample  \t\e\x\t and \\ \- \~
.
<p md-pos="0-30">Sample  \t\e\x\t and \ - ~</p>
.
Document[0, 30]
  Paragraph[0, 30]
    TextBase[0, 29] chars:[0, 29, "Sampl … \- \~"]
      Text[0, 21] chars:[0, 21, "Sampl …  and "]
      EscapedCharacter[21, 23] textOpen:[21, 22, "\"] text:[22, 23, "\"]
      Text[23, 24] chars:[23, 24, " "]
      EscapedCharacter[24, 26] textOpen:[24, 25, "\"] text:[25, 26, "-"]
      Text[26, 27] chars:[26, 27, " "]
      EscapedCharacter[27, 29] textOpen:[27, 28, "\"] text:[28, 29, "~"]
````````````````````````````````


## Issues

#19, ArrayIndexOutOfBounds while parsing markdown with backslash as last character of text block

```````````````````````````````` example Issues: 1
- some item
    - some sub item
      continuation        \
    - another sub item
.
<ul>
  <li>some item
    <ul>
      <li>some sub item
      continuation        \</li>
      <li>another sub item</li>
    </ul>
  </li>
</ul>
.
Document[0, 83]
  BulletList[0, 83] isTight
    BulletListItem[0, 83] open:[0, 1, "-"] isTight
      Paragraph[2, 12]
        Text[2, 11] chars:[2, 11, "some item"]
      BulletList[16, 83] isTight
        BulletListItem[16, 60] open:[16, 17, "-"] isTight
          Paragraph[18, 60]
            Text[18, 31] chars:[18, 31, "some  …  item"]
            SoftLineBreak[31, 32]
            Text[38, 59] chars:[38, 59, "conti …     \"]
        BulletListItem[64, 83] open:[64, 65, "-"] isTight
          Paragraph[66, 83]
            Text[66, 82] chars:[66, 82, "anoth …  item"]
````````````````````````````````


