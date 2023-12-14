

package name.fraser.neil.plaintext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class diff_match_patch {

  
  

  
  public float Diff_Timeout = 1.0f;
  
  public short Diff_EditCost = 4;
  
  public float Match_Threshold = 0.5f;
  
  public int Match_Distance = 1000;
  
  public float Patch_DeleteThreshold = 0.5f;
  
  public short Patch_Margin = 4;

  
  private short Match_MaxBits = 32;

  
  protected static class LinesToCharsResult {
    protected String chars1;
    protected String chars2;
    protected List<String> lineArray;

    protected LinesToCharsResult(String chars1, String chars2,
        List<String> lineArray) {
      this.chars1 = chars1;
      this.chars2 = chars2;
      this.lineArray = lineArray;
    }
  }


  


  
  public enum Operation {
    DELETE, INSERT, EQUAL
  }

  
  public LinkedList<Diff> diff_main(String text1, String text2) {
    return diff_main(text1, text2, true);
  }

  
  public LinkedList<Diff> diff_main(String text1, String text2,
                                    boolean checklines) {
    
    long deadline;
    if (Diff_Timeout <= 0) {
      deadline = Long.MAX_VALUE;
    } else {
      deadline = System.currentTimeMillis() + (long) (Diff_Timeout * 1000);
    }
    return diff_main(text1, text2, checklines, deadline);
  }

  
  private LinkedList<Diff> diff_main(String text1, String text2,
                                     boolean checklines, long deadline) {
    
    if (text1 == null || text2 == null) {
      throw new IllegalArgumentException("Null inputs. (diff_main)");
    }

    
    LinkedList<Diff> diffs;
    if (text1.equals(text2)) {
      diffs = new LinkedList<Diff>();
      if (text1.length() != 0) {
        diffs.add(new Diff(Operation.EQUAL, text1));
      }
      return diffs;
    }

    
    int commonlength = diff_commonPrefix(text1, text2);
    String commonprefix = text1.substring(0, commonlength);
    text1 = text1.substring(commonlength);
    text2 = text2.substring(commonlength);

    
    commonlength = diff_commonSuffix(text1, text2);
    String commonsuffix = text1.substring(text1.length() - commonlength);
    text1 = text1.substring(0, text1.length() - commonlength);
    text2 = text2.substring(0, text2.length() - commonlength);

    
    diffs = diff_compute(text1, text2, checklines, deadline);

    
    if (commonprefix.length() != 0) {
      diffs.addFirst(new Diff(Operation.EQUAL, commonprefix));
    }
    if (commonsuffix.length() != 0) {
      diffs.addLast(new Diff(Operation.EQUAL, commonsuffix));
    }

    diff_cleanupMerge(diffs);
    return diffs;
  }

  
  private LinkedList<Diff> diff_compute(String text1, String text2,
                                        boolean checklines, long deadline) {
    LinkedList<Diff> diffs = new LinkedList<Diff>();

    if (text1.length() == 0) {
      
      diffs.add(new Diff(Operation.INSERT, text2));
      return diffs;
    }

    if (text2.length() == 0) {
      
      diffs.add(new Diff(Operation.DELETE, text1));
      return diffs;
    }

    String longtext = text1.length() > text2.length() ? text1 : text2;
    String shorttext = text1.length() > text2.length() ? text2 : text1;
    int i = longtext.indexOf(shorttext);
    if (i != -1) {
      
      Operation op = (text1.length() > text2.length()) ?
                     Operation.DELETE : Operation.INSERT;
      diffs.add(new Diff(op, longtext.substring(0, i)));
      diffs.add(new Diff(Operation.EQUAL, shorttext));
      diffs.add(new Diff(op, longtext.substring(i + shorttext.length())));
      return diffs;
    }

    if (shorttext.length() == 1) {
      
      
      diffs.add(new Diff(Operation.DELETE, text1));
      diffs.add(new Diff(Operation.INSERT, text2));
      return diffs;
    }

    
    String[] hm = diff_halfMatch(text1, text2);
    if (hm != null) {
      
      String text1_a = hm[0];
      String text1_b = hm[1];
      String text2_a = hm[2];
      String text2_b = hm[3];
      String mid_common = hm[4];
      
      LinkedList<Diff> diffs_a = diff_main(text1_a, text2_a,
                                           checklines, deadline);
      LinkedList<Diff> diffs_b = diff_main(text1_b, text2_b,
                                           checklines, deadline);
      
      diffs = diffs_a;
      diffs.add(new Diff(Operation.EQUAL, mid_common));
      diffs.addAll(diffs_b);
      return diffs;
    }

    if (checklines && text1.length() > 100 && text2.length() > 100) {
      return diff_lineMode(text1, text2, deadline);
    }

    return diff_bisect(text1, text2, deadline);
  }

  
  private LinkedList<Diff> diff_lineMode(String text1, String text2,
                                         long deadline) {
    
    LinesToCharsResult a = diff_linesToChars(text1, text2);
    text1 = a.chars1;
    text2 = a.chars2;
    List<String> linearray = a.lineArray;

    LinkedList<Diff> diffs = diff_main(text1, text2, false, deadline);

    
    diff_charsToLines(diffs, linearray);
    
    diff_cleanupSemantic(diffs);

    
    
    diffs.add(new Diff(Operation.EQUAL, ""));
    int count_delete = 0;
    int count_insert = 0;
    String text_delete = "";
    String text_insert = "";
    ListIterator<Diff> pointer = diffs.listIterator();
    Diff thisDiff = pointer.next();
    while (thisDiff != null) {
      switch (thisDiff.operation) {
      case INSERT:
        count_insert++;
        text_insert += thisDiff.text;
        break;
      case DELETE:
        count_delete++;
        text_delete += thisDiff.text;
        break;
      case EQUAL:
        
        if (count_delete >= 1 && count_insert >= 1) {
          
          pointer.previous();
          for (int j = 0; j < count_delete + count_insert; j++) {
            pointer.previous();
            pointer.remove();
          }
          for (Diff subDiff : diff_main(text_delete, text_insert, false,
              deadline)) {
            pointer.add(subDiff);
          }
        }
        count_insert = 0;
        count_delete = 0;
        text_delete = "";
        text_insert = "";
        break;
      }
      thisDiff = pointer.hasNext() ? pointer.next() : null;
    }
    diffs.removeLast();  

    return diffs;
  }

  
  protected LinkedList<Diff> diff_bisect(String text1, String text2,
      long deadline) {
    
    int text1_length = text1.length();
    int text2_length = text2.length();
    int max_d = (text1_length + text2_length + 1) / 2;
    int v_offset = max_d;
    int v_length = 2 * max_d;
    int[] v1 = new int[v_length];
    int[] v2 = new int[v_length];
    for (int x = 0; x < v_length; x++) {
      v1[x] = -1;
      v2[x] = -1;
    }
    v1[v_offset + 1] = 0;
    v2[v_offset + 1] = 0;
    int delta = text1_length - text2_length;
    
    
    boolean front = (delta % 2 != 0);
    
    
    int k1start = 0;
    int k1end = 0;
    int k2start = 0;
    int k2end = 0;
    for (int d = 0; d < max_d; d++) {
      
      if (System.currentTimeMillis() > deadline) {
        break;
      }

      
      for (int k1 = -d + k1start; k1 <= d - k1end; k1 += 2) {
        int k1_offset = v_offset + k1;
        int x1;
        if (k1 == -d || (k1 != d && v1[k1_offset - 1] < v1[k1_offset + 1])) {
          x1 = v1[k1_offset + 1];
        } else {
          x1 = v1[k1_offset - 1] + 1;
        }
        int y1 = x1 - k1;
        while (x1 < text1_length && y1 < text2_length
               && text1.charAt(x1) == text2.charAt(y1)) {
          x1++;
          y1++;
        }
        v1[k1_offset] = x1;
        if (x1 > text1_length) {
          
          k1end += 2;
        } else if (y1 > text2_length) {
          
          k1start += 2;
        } else if (front) {
          int k2_offset = v_offset + delta - k1;
          if (k2_offset >= 0 && k2_offset < v_length && v2[k2_offset] != -1) {
            
            int x2 = text1_length - v2[k2_offset];
            if (x1 >= x2) {
              
              return diff_bisectSplit(text1, text2, x1, y1, deadline);
            }
          }
        }
      }

      
      for (int k2 = -d + k2start; k2 <= d - k2end; k2 += 2) {
        int k2_offset = v_offset + k2;
        int x2;
        if (k2 == -d || (k2 != d && v2[k2_offset - 1] < v2[k2_offset + 1])) {
          x2 = v2[k2_offset + 1];
        } else {
          x2 = v2[k2_offset - 1] + 1;
        }
        int y2 = x2 - k2;
        while (x2 < text1_length && y2 < text2_length
               && text1.charAt(text1_length - x2 - 1)
               == text2.charAt(text2_length - y2 - 1)) {
          x2++;
          y2++;
        }
        v2[k2_offset] = x2;
        if (x2 > text1_length) {
          
          k2end += 2;
        } else if (y2 > text2_length) {
          
          k2start += 2;
        } else if (!front) {
          int k1_offset = v_offset + delta - k2;
          if (k1_offset >= 0 && k1_offset < v_length && v1[k1_offset] != -1) {
            int x1 = v1[k1_offset];
            int y1 = v_offset + x1 - k1_offset;
            
            x2 = text1_length - x2;
            if (x1 >= x2) {
              
              return diff_bisectSplit(text1, text2, x1, y1, deadline);
            }
          }
        }
      }
    }
    
    
    LinkedList<Diff> diffs = new LinkedList<Diff>();
    diffs.add(new Diff(Operation.DELETE, text1));
    diffs.add(new Diff(Operation.INSERT, text2));
    return diffs;
  }

  
  private LinkedList<Diff> diff_bisectSplit(String text1, String text2,
                                            int x, int y, long deadline) {
    String text1a = text1.substring(0, x);
    String text2a = text2.substring(0, y);
    String text1b = text1.substring(x);
    String text2b = text2.substring(y);

    
    LinkedList<Diff> diffs = diff_main(text1a, text2a, false, deadline);
    LinkedList<Diff> diffsb = diff_main(text1b, text2b, false, deadline);

    diffs.addAll(diffsb);
    return diffs;
  }

  
  protected LinesToCharsResult diff_linesToChars(String text1, String text2) {
    List<String> lineArray = new ArrayList<String>();
    Map<String, Integer> lineHash = new HashMap<String, Integer>();
    
    

    
    
    lineArray.add("");

    
    String chars1 = diff_linesToCharsMunge(text1, lineArray, lineHash, 40000);
    String chars2 = diff_linesToCharsMunge(text2, lineArray, lineHash, 65535);
    return new LinesToCharsResult(chars1, chars2, lineArray);
  }

  
  private String diff_linesToCharsMunge(String text, List<String> lineArray,
      Map<String, Integer> lineHash, int maxLines) {
    int lineStart = 0;
    int lineEnd = -1;
    String line;
    StringBuilder chars = new StringBuilder();
    
    
    
    while (lineEnd < text.length() - 1) {
      lineEnd = text.indexOf('\n', lineStart);
      if (lineEnd == -1) {
        lineEnd = text.length() - 1;
      }
      line = text.substring(lineStart, lineEnd + 1);

      if (lineHash.containsKey(line)) {
        chars.append(String.valueOf((char) (int) lineHash.get(line)));
      } else {
        if (lineArray.size() == maxLines) {
          
          
          line = text.substring(lineStart);
          lineEnd = text.length();
        }
        lineArray.add(line);
        lineHash.put(line, lineArray.size() - 1);
        chars.append(String.valueOf((char) (lineArray.size() - 1)));
      }
      lineStart = lineEnd + 1;
    }
    return chars.toString();
  }

  
  protected void diff_charsToLines(List<Diff> diffs,
                                  List<String> lineArray) {
    StringBuilder text;
    for (Diff diff : diffs) {
      text = new StringBuilder();
      for (int j = 0; j < diff.text.length(); j++) {
        text.append(lineArray.get(diff.text.charAt(j)));
      }
      diff.text = text.toString();
    }
  }

  
  public int diff_commonPrefix(String text1, String text2) {
    
    int n = Math.min(text1.length(), text2.length());
    for (int i = 0; i < n; i++) {
      if (text1.charAt(i) != text2.charAt(i)) {
        return i;
      }
    }
    return n;
  }

  
  public int diff_commonSuffix(String text1, String text2) {
    
    int text1_length = text1.length();
    int text2_length = text2.length();
    int n = Math.min(text1_length, text2_length);
    for (int i = 1; i <= n; i++) {
      if (text1.charAt(text1_length - i) != text2.charAt(text2_length - i)) {
        return i - 1;
      }
    }
    return n;
  }

  
  protected int diff_commonOverlap(String text1, String text2) {
    
    int text1_length = text1.length();
    int text2_length = text2.length();
    
    if (text1_length == 0 || text2_length == 0) {
      return 0;
    }
    
    if (text1_length > text2_length) {
      text1 = text1.substring(text1_length - text2_length);
    } else if (text1_length < text2_length) {
      text2 = text2.substring(0, text1_length);
    }
    int text_length = Math.min(text1_length, text2_length);
    
    if (text1.equals(text2)) {
      return text_length;
    }

    
    
    
    int best = 0;
    int length = 1;
    while (true) {
      String pattern = text1.substring(text_length - length);
      int found = text2.indexOf(pattern);
      if (found == -1) {
        return best;
      }
      length += found;
      if (found == 0 || text1.substring(text_length - length).equals(
          text2.substring(0, length))) {
        best = length;
        length++;
      }
    }
  }

  
  protected String[] diff_halfMatch(String text1, String text2) {
    if (Diff_Timeout <= 0) {
      
      return null;
    }
    String longtext = text1.length() > text2.length() ? text1 : text2;
    String shorttext = text1.length() > text2.length() ? text2 : text1;
    if (longtext.length() < 4 || shorttext.length() * 2 < longtext.length()) {
      return null;  
    }

    
    String[] hm1 = diff_halfMatchI(longtext, shorttext,
                                   (longtext.length() + 3) / 4);
    
    String[] hm2 = diff_halfMatchI(longtext, shorttext,
                                   (longtext.length() + 1) / 2);
    String[] hm;
    if (hm1 == null && hm2 == null) {
      return null;
    } else if (hm2 == null) {
      hm = hm1;
    } else if (hm1 == null) {
      hm = hm2;
    } else {
      
      hm = hm1[4].length() > hm2[4].length() ? hm1 : hm2;
    }

    
    if (text1.length() > text2.length()) {
      return hm;
      
    } else {
      return new String[]{hm[2], hm[3], hm[0], hm[1], hm[4]};
    }
  }

  
  private String[] diff_halfMatchI(String longtext, String shorttext, int i) {
    
    String seed = longtext.substring(i, i + longtext.length() / 4);
    int j = -1;
    String best_common = "";
    String best_longtext_a = "", best_longtext_b = "";
    String best_shorttext_a = "", best_shorttext_b = "";
    while ((j = shorttext.indexOf(seed, j + 1)) != -1) {
      int prefixLength = diff_commonPrefix(longtext.substring(i),
                                           shorttext.substring(j));
      int suffixLength = diff_commonSuffix(longtext.substring(0, i),
                                           shorttext.substring(0, j));
      if (best_common.length() < suffixLength + prefixLength) {
        best_common = shorttext.substring(j - suffixLength, j)
            + shorttext.substring(j, j + prefixLength);
        best_longtext_a = longtext.substring(0, i - suffixLength);
        best_longtext_b = longtext.substring(i + prefixLength);
        best_shorttext_a = shorttext.substring(0, j - suffixLength);
        best_shorttext_b = shorttext.substring(j + prefixLength);
      }
    }
    if (best_common.length() * 2 >= longtext.length()) {
      return new String[]{best_longtext_a, best_longtext_b,
                          best_shorttext_a, best_shorttext_b, best_common};
    } else {
      return null;
    }
  }

  
  public void diff_cleanupSemantic(LinkedList<Diff> diffs) {
    if (diffs.isEmpty()) {
      return;
    }
    boolean changes = false;
    Deque<Diff> equalities = new ArrayDeque<Diff>();  
    String lastEquality = null; 
    ListIterator<Diff> pointer = diffs.listIterator();
    
    int length_insertions1 = 0;
    int length_deletions1 = 0;
    
    int length_insertions2 = 0;
    int length_deletions2 = 0;
    Diff thisDiff = pointer.next();
    while (thisDiff != null) {
      if (thisDiff.operation == Operation.EQUAL) {
        
        equalities.push(thisDiff);
        length_insertions1 = length_insertions2;
        length_deletions1 = length_deletions2;
        length_insertions2 = 0;
        length_deletions2 = 0;
        lastEquality = thisDiff.text;
      } else {
        
        if (thisDiff.operation == Operation.INSERT) {
          length_insertions2 += thisDiff.text.length();
        } else {
          length_deletions2 += thisDiff.text.length();
        }
        
        
        if (lastEquality != null && (lastEquality.length()
            <= Math.max(length_insertions1, length_deletions1))
            && (lastEquality.length()
                <= Math.max(length_insertions2, length_deletions2))) {
          
          
          while (thisDiff != equalities.peek()) {
            thisDiff = pointer.previous();
          }
          pointer.next();

          
          pointer.set(new Diff(Operation.DELETE, lastEquality));
          
          pointer.add(new Diff(Operation.INSERT, lastEquality));

          equalities.pop();  
          if (!equalities.isEmpty()) {
            
            equalities.pop();
          }
          if (equalities.isEmpty()) {
            
            while (pointer.hasPrevious()) {
              pointer.previous();
            }
          } else {
            
            thisDiff = equalities.peek();
            while (thisDiff != pointer.previous()) {
              
            }
          }

          length_insertions1 = 0;  
          length_insertions2 = 0;
          length_deletions1 = 0;
          length_deletions2 = 0;
          lastEquality = null;
          changes = true;
        }
      }
      thisDiff = pointer.hasNext() ? pointer.next() : null;
    }

    
    if (changes) {
      diff_cleanupMerge(diffs);
    }
    diff_cleanupSemanticLossless(diffs);

    
    
    
    
    
    
    pointer = diffs.listIterator();
    Diff prevDiff = null;
    thisDiff = null;
    if (pointer.hasNext()) {
      prevDiff = pointer.next();
      if (pointer.hasNext()) {
        thisDiff = pointer.next();
      }
    }
    while (thisDiff != null) {
      if (prevDiff.operation == Operation.DELETE &&
          thisDiff.operation == Operation.INSERT) {
        String deletion = prevDiff.text;
        String insertion = thisDiff.text;
        int overlap_length1 = this.diff_commonOverlap(deletion, insertion);
        int overlap_length2 = this.diff_commonOverlap(insertion, deletion);
        if (overlap_length1 >= overlap_length2) {
          if (overlap_length1 >= deletion.length() / 2.0 ||
              overlap_length1 >= insertion.length() / 2.0) {
            
            pointer.previous();
            pointer.add(new Diff(Operation.EQUAL,
                                 insertion.substring(0, overlap_length1)));
            prevDiff.text =
                deletion.substring(0, deletion.length() - overlap_length1);
            thisDiff.text = insertion.substring(overlap_length1);
            
            
          }
        } else {
          if (overlap_length2 >= deletion.length() / 2.0 ||
              overlap_length2 >= insertion.length() / 2.0) {
            
            
            pointer.previous();
            pointer.add(new Diff(Operation.EQUAL,
                                 deletion.substring(0, overlap_length2)));
            prevDiff.operation = Operation.INSERT;
            prevDiff.text =
              insertion.substring(0, insertion.length() - overlap_length2);
            thisDiff.operation = Operation.DELETE;
            thisDiff.text = deletion.substring(overlap_length2);
            
            
          }
        }
        thisDiff = pointer.hasNext() ? pointer.next() : null;
      }
      prevDiff = thisDiff;
      thisDiff = pointer.hasNext() ? pointer.next() : null;
    }
  }

  
  public void diff_cleanupSemanticLossless(LinkedList<Diff> diffs) {
    String equality1, edit, equality2;
    String commonString;
    int commonOffset;
    int score, bestScore;
    String bestEquality1, bestEdit, bestEquality2;
    
    ListIterator<Diff> pointer = diffs.listIterator();
    Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
    Diff thisDiff = pointer.hasNext() ? pointer.next() : null;
    Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
    
    while (nextDiff != null) {
      if (prevDiff.operation == Operation.EQUAL &&
          nextDiff.operation == Operation.EQUAL) {
        
        equality1 = prevDiff.text;
        edit = thisDiff.text;
        equality2 = nextDiff.text;

        
        commonOffset = diff_commonSuffix(equality1, edit);
        if (commonOffset != 0) {
          commonString = edit.substring(edit.length() - commonOffset);
          equality1 = equality1.substring(0, equality1.length() - commonOffset);
          edit = commonString + edit.substring(0, edit.length() - commonOffset);
          equality2 = commonString + equality2;
        }

        
        bestEquality1 = equality1;
        bestEdit = edit;
        bestEquality2 = equality2;
        bestScore = diff_cleanupSemanticScore(equality1, edit)
            + diff_cleanupSemanticScore(edit, equality2);
        while (edit.length() != 0 && equality2.length() != 0
            && edit.charAt(0) == equality2.charAt(0)) {
          equality1 += edit.charAt(0);
          edit = edit.substring(1) + equality2.charAt(0);
          equality2 = equality2.substring(1);
          score = diff_cleanupSemanticScore(equality1, edit)
              + diff_cleanupSemanticScore(edit, equality2);
          
          if (score >= bestScore) {
            bestScore = score;
            bestEquality1 = equality1;
            bestEdit = edit;
            bestEquality2 = equality2;
          }
        }

        if (!prevDiff.text.equals(bestEquality1)) {
          
          if (bestEquality1.length() != 0) {
            prevDiff.text = bestEquality1;
          } else {
            pointer.previous(); 
            pointer.previous(); 
            pointer.previous(); 
            pointer.remove(); 
            pointer.next(); 
            pointer.next(); 
          }
          thisDiff.text = bestEdit;
          if (bestEquality2.length() != 0) {
            nextDiff.text = bestEquality2;
          } else {
            pointer.remove(); 
            nextDiff = thisDiff;
            thisDiff = prevDiff;
          }
        }
      }
      prevDiff = thisDiff;
      thisDiff = nextDiff;
      nextDiff = pointer.hasNext() ? pointer.next() : null;
    }
  }

  
  private int diff_cleanupSemanticScore(String one, String two) {
    if (one.length() == 0 || two.length() == 0) {
      
      return 6;
    }

    
    
    
    
    
    char char1 = one.charAt(one.length() - 1);
    char char2 = two.charAt(0);
    boolean nonAlphaNumeric1 = !Character.isLetterOrDigit(char1);
    boolean nonAlphaNumeric2 = !Character.isLetterOrDigit(char2);
    boolean whitespace1 = nonAlphaNumeric1 && Character.isWhitespace(char1);
    boolean whitespace2 = nonAlphaNumeric2 && Character.isWhitespace(char2);
    boolean lineBreak1 = whitespace1
        && Character.getType(char1) == Character.CONTROL;
    boolean lineBreak2 = whitespace2
        && Character.getType(char2) == Character.CONTROL;
    boolean blankLine1 = lineBreak1 && BLANKLINEEND.matcher(one).find();
    boolean blankLine2 = lineBreak2 && BLANKLINESTART.matcher(two).find();

    if (blankLine1 || blankLine2) {
      
      return 5;
    } else if (lineBreak1 || lineBreak2) {
      
      return 4;
    } else if (nonAlphaNumeric1 && !whitespace1 && whitespace2) {
      
      return 3;
    } else if (whitespace1 || whitespace2) {
      
      return 2;
    } else if (nonAlphaNumeric1 || nonAlphaNumeric2) {
      
      return 1;
    }
    return 0;
  }

  
  private Pattern BLANKLINEEND
      = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
  private Pattern BLANKLINESTART
      = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

  
  public void diff_cleanupEfficiency(LinkedList<Diff> diffs) {
    if (diffs.isEmpty()) {
      return;
    }
    boolean changes = false;
    Deque<Diff> equalities = new ArrayDeque<Diff>();  
    String lastEquality = null; 
    ListIterator<Diff> pointer = diffs.listIterator();
    
    boolean pre_ins = false;
    
    boolean pre_del = false;
    
    boolean post_ins = false;
    
    boolean post_del = false;
    Diff thisDiff = pointer.next();
    Diff safeDiff = thisDiff;  
    while (thisDiff != null) {
      if (thisDiff.operation == Operation.EQUAL) {
        
        if (thisDiff.text.length() < Diff_EditCost && (post_ins || post_del)) {
          
          equalities.push(thisDiff);
          pre_ins = post_ins;
          pre_del = post_del;
          lastEquality = thisDiff.text;
        } else {
          
          equalities.clear();
          lastEquality = null;
          safeDiff = thisDiff;
        }
        post_ins = post_del = false;
      } else {
        
        if (thisDiff.operation == Operation.DELETE) {
          post_del = true;
        } else {
          post_ins = true;
        }
        
        if (lastEquality != null
            && ((pre_ins && pre_del && post_ins && post_del)
                || ((lastEquality.length() < Diff_EditCost / 2)
                    && ((pre_ins ? 1 : 0) + (pre_del ? 1 : 0)
                        + (post_ins ? 1 : 0) + (post_del ? 1 : 0)) == 3))) {
          
          
          while (thisDiff != equalities.peek()) {
            thisDiff = pointer.previous();
          }
          pointer.next();

          
          pointer.set(new Diff(Operation.DELETE, lastEquality));
          
          pointer.add(thisDiff = new Diff(Operation.INSERT, lastEquality));

          equalities.pop();  
          lastEquality = null;
          if (pre_ins && pre_del) {
            
            post_ins = post_del = true;
            equalities.clear();
            safeDiff = thisDiff;
          } else {
            if (!equalities.isEmpty()) {
              
              equalities.pop();
            }
            if (equalities.isEmpty()) {
              
              
              thisDiff = safeDiff;
            } else {
              
              thisDiff = equalities.peek();
            }
            while (thisDiff != pointer.previous()) {
              
            }
            post_ins = post_del = false;
          }

          changes = true;
        }
      }
      thisDiff = pointer.hasNext() ? pointer.next() : null;
    }

    if (changes) {
      diff_cleanupMerge(diffs);
    }
  }

  
  public void diff_cleanupMerge(LinkedList<Diff> diffs) {
    diffs.add(new Diff(Operation.EQUAL, ""));  
    ListIterator<Diff> pointer = diffs.listIterator();
    int count_delete = 0;
    int count_insert = 0;
    String text_delete = "";
    String text_insert = "";
    Diff thisDiff = pointer.next();
    Diff prevEqual = null;
    int commonlength;
    while (thisDiff != null) {
      switch (thisDiff.operation) {
      case INSERT:
        count_insert++;
        text_insert += thisDiff.text;
        prevEqual = null;
        break;
      case DELETE:
        count_delete++;
        text_delete += thisDiff.text;
        prevEqual = null;
        break;
      case EQUAL:
        if (count_delete + count_insert > 1) {
          boolean both_types = count_delete != 0 && count_insert != 0;
          
          pointer.previous();  
          while (count_delete-- > 0) {
            pointer.previous();
            pointer.remove();
          }
          while (count_insert-- > 0) {
            pointer.previous();
            pointer.remove();
          }
          if (both_types) {
            
            commonlength = diff_commonPrefix(text_insert, text_delete);
            if (commonlength != 0) {
              if (pointer.hasPrevious()) {
                thisDiff = pointer.previous();
                assert thisDiff.operation == Operation.EQUAL
                       : "Previous diff should have been an equality.";
                thisDiff.text += text_insert.substring(0, commonlength);
                pointer.next();
              } else {
                pointer.add(new Diff(Operation.EQUAL,
                    text_insert.substring(0, commonlength)));
              }
              text_insert = text_insert.substring(commonlength);
              text_delete = text_delete.substring(commonlength);
            }
            
            commonlength = diff_commonSuffix(text_insert, text_delete);
            if (commonlength != 0) {
              thisDiff = pointer.next();
              thisDiff.text = text_insert.substring(text_insert.length()
                  - commonlength) + thisDiff.text;
              text_insert = text_insert.substring(0, text_insert.length()
                  - commonlength);
              text_delete = text_delete.substring(0, text_delete.length()
                  - commonlength);
              pointer.previous();
            }
          }
          
          if (text_delete.length() != 0) {
            pointer.add(new Diff(Operation.DELETE, text_delete));
          }
          if (text_insert.length() != 0) {
            pointer.add(new Diff(Operation.INSERT, text_insert));
          }
          
          thisDiff = pointer.hasNext() ? pointer.next() : null;
        } else if (prevEqual != null) {
          
          prevEqual.text += thisDiff.text;
          pointer.remove();
          thisDiff = pointer.previous();
          pointer.next();  
        }
        count_insert = 0;
        count_delete = 0;
        text_delete = "";
        text_insert = "";
        prevEqual = thisDiff;
        break;
      }
      thisDiff = pointer.hasNext() ? pointer.next() : null;
    }
    if (diffs.getLast().text.length() == 0) {
      diffs.removeLast();  
    }

    
    boolean changes = false;
    
    
    pointer = diffs.listIterator();
    Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
    thisDiff = pointer.hasNext() ? pointer.next() : null;
    Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
    
    while (nextDiff != null) {
      if (prevDiff.operation == Operation.EQUAL &&
          nextDiff.operation == Operation.EQUAL) {
        
        if (thisDiff.text.endsWith(prevDiff.text)) {
          
          thisDiff.text = prevDiff.text
              + thisDiff.text.substring(0, thisDiff.text.length()
                                           - prevDiff.text.length());
          nextDiff.text = prevDiff.text + nextDiff.text;
          pointer.previous(); 
          pointer.previous(); 
          pointer.previous(); 
          pointer.remove(); 
          pointer.next(); 
          thisDiff = pointer.next(); 
          nextDiff = pointer.hasNext() ? pointer.next() : null;
          changes = true;
        } else if (thisDiff.text.startsWith(nextDiff.text)) {
          
          prevDiff.text += nextDiff.text;
          thisDiff.text = thisDiff.text.substring(nextDiff.text.length())
              + nextDiff.text;
          pointer.remove(); 
          nextDiff = pointer.hasNext() ? pointer.next() : null;
          changes = true;
        }
      }
      prevDiff = thisDiff;
      thisDiff = nextDiff;
      nextDiff = pointer.hasNext() ? pointer.next() : null;
    }
    
    if (changes) {
      diff_cleanupMerge(diffs);
    }
  }

  
  public int diff_xIndex(List<Diff> diffs, int loc) {
    int chars1 = 0;
    int chars2 = 0;
    int last_chars1 = 0;
    int last_chars2 = 0;
    Diff lastDiff = null;
    for (Diff aDiff : diffs) {
      if (aDiff.operation != Operation.INSERT) {
        
        chars1 += aDiff.text.length();
      }
      if (aDiff.operation != Operation.DELETE) {
        
        chars2 += aDiff.text.length();
      }
      if (chars1 > loc) {
        
        lastDiff = aDiff;
        break;
      }
      last_chars1 = chars1;
      last_chars2 = chars2;
    }
    if (lastDiff != null && lastDiff.operation == Operation.DELETE) {
      
      return last_chars2;
    }
    
    return last_chars2 + (loc - last_chars1);
  }

  
  public String diff_prettyHtml(List<Diff> diffs) {
    StringBuilder html = new StringBuilder();
    for (Diff aDiff : diffs) {
      String text = aDiff.text.replace("&", "&amp;").replace("<", "&lt;")
          .replace(">", "&gt;").replace("\n", "&para;<br>");
      switch (aDiff.operation) {
      case INSERT:
        html.append("<ins style=\"background:#e6ffe6;\">").append(text)
            .append("</ins>");
        break;
      case DELETE:
        html.append("<del style=\"background:#ffe6e6;\">").append(text)
            .append("</del>");
        break;
      case EQUAL:
        html.append("<span>").append(text).append("</span>");
        break;
      }
    }
    return html.toString();
  }

  
  public String diff_text1(List<Diff> diffs) {
    StringBuilder text = new StringBuilder();
    for (Diff aDiff : diffs) {
      if (aDiff.operation != Operation.INSERT) {
        text.append(aDiff.text);
      }
    }
    return text.toString();
  }

  
  public String diff_text2(List<Diff> diffs) {
    StringBuilder text = new StringBuilder();
    for (Diff aDiff : diffs) {
      if (aDiff.operation != Operation.DELETE) {
        text.append(aDiff.text);
      }
    }
    return text.toString();
  }

  
  public int diff_levenshtein(List<Diff> diffs) {
    int levenshtein = 0;
    int insertions = 0;
    int deletions = 0;
    for (Diff aDiff : diffs) {
      switch (aDiff.operation) {
      case INSERT:
        insertions += aDiff.text.length();
        break;
      case DELETE:
        deletions += aDiff.text.length();
        break;
      case EQUAL:
        
        levenshtein += Math.max(insertions, deletions);
        insertions = 0;
        deletions = 0;
        break;
      }
    }
    levenshtein += Math.max(insertions, deletions);
    return levenshtein;
  }

  
  public String diff_toDelta(List<Diff> diffs) {
    StringBuilder text = new StringBuilder();
    for (Diff aDiff : diffs) {
      switch (aDiff.operation) {
      case INSERT:
        try {
          text.append("+").append(URLEncoder.encode(aDiff.text, "UTF-8")
                                            .replace('+', ' ')).append("\t");
        } catch (UnsupportedEncodingException e) {
          
          throw new Error("This system does not support UTF-8.", e);
        }
        break;
      case DELETE:
        text.append("-").append(aDiff.text.length()).append("\t");
        break;
      case EQUAL:
        text.append("=").append(aDiff.text.length()).append("\t");
        break;
      }
    }
    String delta = text.toString();
    if (delta.length() != 0) {
      
      delta = delta.substring(0, delta.length() - 1);
      delta = unescapeForEncodeUriCompatability(delta);
    }
    return delta;
  }

  
  public LinkedList<Diff> diff_fromDelta(String text1, String delta)
      throws IllegalArgumentException {
    LinkedList<Diff> diffs = new LinkedList<Diff>();
    int pointer = 0;  
    String[] tokens = delta.split("\t");
    for (String token : tokens) {
      if (token.length() == 0) {
        
        continue;
      }
      
      
      String param = token.substring(1);
      switch (token.charAt(0)) {
      case '+':
        
        param = param.replace("+", "%2B");
        try {
          param = URLDecoder.decode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          
          throw new Error("This system does not support UTF-8.", e);
        } catch (IllegalArgumentException e) {
          
          throw new IllegalArgumentException(
              "Illegal escape in diff_fromDelta: " + param, e);
        }
        diffs.add(new Diff(Operation.INSERT, param));
        break;
      case '-':
        
      case '=':
        int n;
        try {
          n = Integer.parseInt(param);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Invalid number in diff_fromDelta: " + param, e);
        }
        if (n < 0) {
          throw new IllegalArgumentException(
              "Negative number in diff_fromDelta: " + param);
        }
        String text;
        try {
          text = text1.substring(pointer, pointer += n);
        } catch (StringIndexOutOfBoundsException e) {
          throw new IllegalArgumentException("Delta length (" + pointer
              + ") larger than source text length (" + text1.length()
              + ").", e);
        }
        if (token.charAt(0) == '=') {
          diffs.add(new Diff(Operation.EQUAL, text));
        } else {
          diffs.add(new Diff(Operation.DELETE, text));
        }
        break;
      default:
        
        throw new IllegalArgumentException(
            "Invalid diff operation in diff_fromDelta: " + token.charAt(0));
      }
    }
    if (pointer != text1.length()) {
      throw new IllegalArgumentException("Delta length (" + pointer
          + ") smaller than source text length (" + text1.length() + ").");
    }
    return diffs;
  }


  


  
  public int match_main(String text, String pattern, int loc) {
    
    if (text == null || pattern == null) {
      throw new IllegalArgumentException("Null inputs. (match_main)");
    }

    loc = Math.max(0, Math.min(loc, text.length()));
    if (text.equals(pattern)) {
      
      return 0;
    } else if (text.length() == 0) {
      
      return -1;
    } else if (loc + pattern.length() <= text.length()
        && text.substring(loc, loc + pattern.length()).equals(pattern)) {
      
      return loc;
    } else {
      
      return match_bitap(text, pattern, loc);
    }
  }

  
  protected int match_bitap(String text, String pattern, int loc) {
    assert (Match_MaxBits == 0 || pattern.length() <= Match_MaxBits)
        : "Pattern too long for this application.";

    
    Map<Character, Integer> s = match_alphabet(pattern);

    
    double score_threshold = Match_Threshold;
    
    int best_loc = text.indexOf(pattern, loc);
    if (best_loc != -1) {
      score_threshold = Math.min(match_bitapScore(0, best_loc, loc, pattern),
          score_threshold);
      
      best_loc = text.lastIndexOf(pattern, loc + pattern.length());
      if (best_loc != -1) {
        score_threshold = Math.min(match_bitapScore(0, best_loc, loc, pattern),
            score_threshold);
      }
    }

    
    int matchmask = 1 << (pattern.length() - 1);
    best_loc = -1;

    int bin_min, bin_mid;
    int bin_max = pattern.length() + text.length();
    
    int[] last_rd = new int[0];
    for (int d = 0; d < pattern.length(); d++) {
      
      
      
      bin_min = 0;
      bin_mid = bin_max;
      while (bin_min < bin_mid) {
        if (match_bitapScore(d, loc + bin_mid, loc, pattern)
            <= score_threshold) {
          bin_min = bin_mid;
        } else {
          bin_max = bin_mid;
        }
        bin_mid = (bin_max - bin_min) / 2 + bin_min;
      }
      
      bin_max = bin_mid;
      int start = Math.max(1, loc - bin_mid + 1);
      int finish = Math.min(loc + bin_mid, text.length()) + pattern.length();

      int[] rd = new int[finish + 2];
      rd[finish + 1] = (1 << d) - 1;
      for (int j = finish; j >= start; j--) {
        int charMatch;
        if (text.length() <= j - 1 || !s.containsKey(text.charAt(j - 1))) {
          
          charMatch = 0;
        } else {
          charMatch = s.get(text.charAt(j - 1));
        }
        if (d == 0) {
          
          rd[j] = ((rd[j + 1] << 1) | 1) & charMatch;
        } else {
          
          rd[j] = (((rd[j + 1] << 1) | 1) & charMatch)
              | (((last_rd[j + 1] | last_rd[j]) << 1) | 1) | last_rd[j + 1];
        }
        if ((rd[j] & matchmask) != 0) {
          double score = match_bitapScore(d, j - 1, loc, pattern);
          
          
          if (score <= score_threshold) {
            
            score_threshold = score;
            best_loc = j - 1;
            if (best_loc > loc) {
              
              start = Math.max(1, 2 * loc - best_loc);
            } else {
              
              break;
            }
          }
        }
      }
      if (match_bitapScore(d + 1, loc, loc, pattern) > score_threshold) {
        
        break;
      }
      last_rd = rd;
    }
    return best_loc;
  }

  
  private double match_bitapScore(int e, int x, int loc, String pattern) {
    float accuracy = (float) e / pattern.length();
    int proximity = Math.abs(loc - x);
    if (Match_Distance == 0) {
      
      return proximity == 0 ? accuracy : 1.0;
    }
    return accuracy + (proximity / (float) Match_Distance);
  }

  
  protected Map<Character, Integer> match_alphabet(String pattern) {
    Map<Character, Integer> s = new HashMap<Character, Integer>();
    char[] char_pattern = pattern.toCharArray();
    for (char c : char_pattern) {
      s.put(c, 0);
    }
    int i = 0;
    for (char c : char_pattern) {
      s.put(c, s.get(c) | (1 << (pattern.length() - i - 1)));
      i++;
    }
    return s;
  }


  


  
  protected void patch_addContext(Patch patch, String text) {
    if (text.length() == 0) {
      return;
    }
    String pattern = text.substring(patch.start2, patch.start2 + patch.length1);
    int padding = 0;

    
    
    while (text.indexOf(pattern) != text.lastIndexOf(pattern)
        && pattern.length() < Match_MaxBits - Patch_Margin - Patch_Margin) {
      padding += Patch_Margin;
      pattern = text.substring(Math.max(0, patch.start2 - padding),
          Math.min(text.length(), patch.start2 + patch.length1 + padding));
    }
    
    padding += Patch_Margin;

    
    String prefix = text.substring(Math.max(0, patch.start2 - padding),
        patch.start2);
    if (prefix.length() != 0) {
      patch.diffs.addFirst(new Diff(Operation.EQUAL, prefix));
    }
    
    String suffix = text.substring(patch.start2 + patch.length1,
        Math.min(text.length(), patch.start2 + patch.length1 + padding));
    if (suffix.length() != 0) {
      patch.diffs.addLast(new Diff(Operation.EQUAL, suffix));
    }

    
    patch.start1 -= prefix.length();
    patch.start2 -= prefix.length();
    
    patch.length1 += prefix.length() + suffix.length();
    patch.length2 += prefix.length() + suffix.length();
  }

  
  public LinkedList<Patch> patch_make(String text1, String text2) {
    if (text1 == null || text2 == null) {
      throw new IllegalArgumentException("Null inputs. (patch_make)");
    }
    
    LinkedList<Diff> diffs = diff_main(text1, text2, true);
    if (diffs.size() > 2) {
      diff_cleanupSemantic(diffs);
      diff_cleanupEfficiency(diffs);
    }
    return patch_make(text1, diffs);
  }

  
  public LinkedList<Patch> patch_make(LinkedList<Diff> diffs) {
    if (diffs == null) {
      throw new IllegalArgumentException("Null inputs. (patch_make)");
    }
    
    String text1 = diff_text1(diffs);
    return patch_make(text1, diffs);
  }

  
  @Deprecated public LinkedList<Patch> patch_make(String text1, String text2,
      LinkedList<Diff> diffs) {
    return patch_make(text1, diffs);
  }

  
  public LinkedList<Patch> patch_make(String text1, LinkedList<Diff> diffs) {
    if (text1 == null || diffs == null) {
      throw new IllegalArgumentException("Null inputs. (patch_make)");
    }

    LinkedList<Patch> patches = new LinkedList<Patch>();
    if (diffs.isEmpty()) {
      return patches;  
    }
    Patch patch = new Patch();
    int char_count1 = 0;  
    int char_count2 = 0;  
    
    
    
    String prepatch_text = text1;
    String postpatch_text = text1;
    for (Diff aDiff : diffs) {
      if (patch.diffs.isEmpty() && aDiff.operation != Operation.EQUAL) {
        
        patch.start1 = char_count1;
        patch.start2 = char_count2;
      }

      switch (aDiff.operation) {
      case INSERT:
        patch.diffs.add(aDiff);
        patch.length2 += aDiff.text.length();
        postpatch_text = postpatch_text.substring(0, char_count2)
            + aDiff.text + postpatch_text.substring(char_count2);
        break;
      case DELETE:
        patch.length1 += aDiff.text.length();
        patch.diffs.add(aDiff);
        postpatch_text = postpatch_text.substring(0, char_count2)
            + postpatch_text.substring(char_count2 + aDiff.text.length());
        break;
      case EQUAL:
        if (aDiff.text.length() <= 2 * Patch_Margin
            && !patch.diffs.isEmpty() && aDiff != diffs.getLast()) {
          
          patch.diffs.add(aDiff);
          patch.length1 += aDiff.text.length();
          patch.length2 += aDiff.text.length();
        }

        if (aDiff.text.length() >= 2 * Patch_Margin && !patch.diffs.isEmpty()) {
          
          if (!patch.diffs.isEmpty()) {
            patch_addContext(patch, prepatch_text);
            patches.add(patch);
            patch = new Patch();
            
            
            
            
            prepatch_text = postpatch_text;
            char_count1 = char_count2;
          }
        }
        break;
      }

      
      if (aDiff.operation != Operation.INSERT) {
        char_count1 += aDiff.text.length();
      }
      if (aDiff.operation != Operation.DELETE) {
        char_count2 += aDiff.text.length();
      }
    }
    
    if (!patch.diffs.isEmpty()) {
      patch_addContext(patch, prepatch_text);
      patches.add(patch);
    }

    return patches;
  }

  
  public LinkedList<Patch> patch_deepCopy(LinkedList<Patch> patches) {
    LinkedList<Patch> patchesCopy = new LinkedList<Patch>();
    for (Patch aPatch : patches) {
      Patch patchCopy = new Patch();
      for (Diff aDiff : aPatch.diffs) {
        Diff diffCopy = new Diff(aDiff.operation, aDiff.text);
        patchCopy.diffs.add(diffCopy);
      }
      patchCopy.start1 = aPatch.start1;
      patchCopy.start2 = aPatch.start2;
      patchCopy.length1 = aPatch.length1;
      patchCopy.length2 = aPatch.length2;
      patchesCopy.add(patchCopy);
    }
    return patchesCopy;
  }

  
  public Object[] patch_apply(LinkedList<Patch> patches, String text) {
    if (patches.isEmpty()) {
      return new Object[]{text, new boolean[0]};
    }

    
    patches = patch_deepCopy(patches);

    String nullPadding = patch_addPadding(patches);
    text = nullPadding + text + nullPadding;
    patch_splitMax(patches);

    int x = 0;
    
    
    
    
    int delta = 0;
    boolean[] results = new boolean[patches.size()];
    for (Patch aPatch : patches) {
      int expected_loc = aPatch.start2 + delta;
      String text1 = diff_text1(aPatch.diffs);
      int start_loc;
      int end_loc = -1;
      if (text1.length() > this.Match_MaxBits) {
        
        
        start_loc = match_main(text,
            text1.substring(0, this.Match_MaxBits), expected_loc);
        if (start_loc != -1) {
          end_loc = match_main(text,
              text1.substring(text1.length() - this.Match_MaxBits),
              expected_loc + text1.length() - this.Match_MaxBits);
          if (end_loc == -1 || start_loc >= end_loc) {
            
            start_loc = -1;
          }
        }
      } else {
        start_loc = match_main(text, text1, expected_loc);
      }
      if (start_loc == -1) {
        
        results[x] = false;
        
        delta -= aPatch.length2 - aPatch.length1;
      } else {
        
        results[x] = true;
        delta = start_loc - expected_loc;
        String text2;
        if (end_loc == -1) {
          text2 = text.substring(start_loc,
              Math.min(start_loc + text1.length(), text.length()));
        } else {
          text2 = text.substring(start_loc,
              Math.min(end_loc + this.Match_MaxBits, text.length()));
        }
        if (text1.equals(text2)) {
          
          text = text.substring(0, start_loc) + diff_text2(aPatch.diffs)
              + text.substring(start_loc + text1.length());
        } else {
          
          
          LinkedList<Diff> diffs = diff_main(text1, text2, false);
          if (text1.length() > this.Match_MaxBits
              && diff_levenshtein(diffs) / (float) text1.length()
              > this.Patch_DeleteThreshold) {
            
            results[x] = false;
          } else {
            diff_cleanupSemanticLossless(diffs);
            int index1 = 0;
            for (Diff aDiff : aPatch.diffs) {
              if (aDiff.operation != Operation.EQUAL) {
                int index2 = diff_xIndex(diffs, index1);
                if (aDiff.operation == Operation.INSERT) {
                  
                  text = text.substring(0, start_loc + index2) + aDiff.text
                      + text.substring(start_loc + index2);
                } else if (aDiff.operation == Operation.DELETE) {
                  
                  text = text.substring(0, start_loc + index2)
                      + text.substring(start_loc + diff_xIndex(diffs,
                      index1 + aDiff.text.length()));
                }
              }
              if (aDiff.operation != Operation.DELETE) {
                index1 += aDiff.text.length();
              }
            }
          }
        }
      }
      x++;
    }
    
    text = text.substring(nullPadding.length(), text.length()
        - nullPadding.length());
    return new Object[]{text, results};
  }

  
  public String patch_addPadding(LinkedList<Patch> patches) {
    short paddingLength = this.Patch_Margin;
    String nullPadding = "";
    for (short x = 1; x <= paddingLength; x++) {
      nullPadding += String.valueOf((char) x);
    }

    
    for (Patch aPatch : patches) {
      aPatch.start1 += paddingLength;
      aPatch.start2 += paddingLength;
    }

    
    Patch patch = patches.getFirst();
    LinkedList<Diff> diffs = patch.diffs;
    if (diffs.isEmpty() || diffs.getFirst().operation != Operation.EQUAL) {
      
      diffs.addFirst(new Diff(Operation.EQUAL, nullPadding));
      patch.start1 -= paddingLength;  
      patch.start2 -= paddingLength;  
      patch.length1 += paddingLength;
      patch.length2 += paddingLength;
    } else if (paddingLength > diffs.getFirst().text.length()) {
      
      Diff firstDiff = diffs.getFirst();
      int extraLength = paddingLength - firstDiff.text.length();
      firstDiff.text = nullPadding.substring(firstDiff.text.length())
          + firstDiff.text;
      patch.start1 -= extraLength;
      patch.start2 -= extraLength;
      patch.length1 += extraLength;
      patch.length2 += extraLength;
    }

    
    patch = patches.getLast();
    diffs = patch.diffs;
    if (diffs.isEmpty() || diffs.getLast().operation != Operation.EQUAL) {
      
      diffs.addLast(new Diff(Operation.EQUAL, nullPadding));
      patch.length1 += paddingLength;
      patch.length2 += paddingLength;
    } else if (paddingLength > diffs.getLast().text.length()) {
      
      Diff lastDiff = diffs.getLast();
      int extraLength = paddingLength - lastDiff.text.length();
      lastDiff.text += nullPadding.substring(0, extraLength);
      patch.length1 += extraLength;
      patch.length2 += extraLength;
    }

    return nullPadding;
  }

  
  public void patch_splitMax(LinkedList<Patch> patches) {
    short patch_size = Match_MaxBits;
    String precontext, postcontext;
    Patch patch;
    int start1, start2;
    boolean empty;
    Operation diff_type;
    String diff_text;
    ListIterator<Patch> pointer = patches.listIterator();
    Patch bigpatch = pointer.hasNext() ? pointer.next() : null;
    while (bigpatch != null) {
      if (bigpatch.length1 <= Match_MaxBits) {
        bigpatch = pointer.hasNext() ? pointer.next() : null;
        continue;
      }
      
      pointer.remove();
      start1 = bigpatch.start1;
      start2 = bigpatch.start2;
      precontext = "";
      while (!bigpatch.diffs.isEmpty()) {
        
        patch = new Patch();
        empty = true;
        patch.start1 = start1 - precontext.length();
        patch.start2 = start2 - precontext.length();
        if (precontext.length() != 0) {
          patch.length1 = patch.length2 = precontext.length();
          patch.diffs.add(new Diff(Operation.EQUAL, precontext));
        }
        while (!bigpatch.diffs.isEmpty()
            && patch.length1 < patch_size - Patch_Margin) {
          diff_type = bigpatch.diffs.getFirst().operation;
          diff_text = bigpatch.diffs.getFirst().text;
          if (diff_type == Operation.INSERT) {
            
            patch.length2 += diff_text.length();
            start2 += diff_text.length();
            patch.diffs.addLast(bigpatch.diffs.removeFirst());
            empty = false;
          } else if (diff_type == Operation.DELETE && patch.diffs.size() == 1
              && patch.diffs.getFirst().operation == Operation.EQUAL
              && diff_text.length() > 2 * patch_size) {
            
            patch.length1 += diff_text.length();
            start1 += diff_text.length();
            empty = false;
            patch.diffs.add(new Diff(diff_type, diff_text));
            bigpatch.diffs.removeFirst();
          } else {
            
            diff_text = diff_text.substring(0, Math.min(diff_text.length(),
                patch_size - patch.length1 - Patch_Margin));
            patch.length1 += diff_text.length();
            start1 += diff_text.length();
            if (diff_type == Operation.EQUAL) {
              patch.length2 += diff_text.length();
              start2 += diff_text.length();
            } else {
              empty = false;
            }
            patch.diffs.add(new Diff(diff_type, diff_text));
            if (diff_text.equals(bigpatch.diffs.getFirst().text)) {
              bigpatch.diffs.removeFirst();
            } else {
              bigpatch.diffs.getFirst().text = bigpatch.diffs.getFirst().text
                  .substring(diff_text.length());
            }
          }
        }
        
        precontext = diff_text2(patch.diffs);
        precontext = precontext.substring(Math.max(0, precontext.length()
            - Patch_Margin));
        
        if (diff_text1(bigpatch.diffs).length() > Patch_Margin) {
          postcontext = diff_text1(bigpatch.diffs).substring(0, Patch_Margin);
        } else {
          postcontext = diff_text1(bigpatch.diffs);
        }
        if (postcontext.length() != 0) {
          patch.length1 += postcontext.length();
          patch.length2 += postcontext.length();
          if (!patch.diffs.isEmpty()
              && patch.diffs.getLast().operation == Operation.EQUAL) {
            patch.diffs.getLast().text += postcontext;
          } else {
            patch.diffs.add(new Diff(Operation.EQUAL, postcontext));
          }
        }
        if (!empty) {
          pointer.add(patch);
        }
      }
      bigpatch = pointer.hasNext() ? pointer.next() : null;
    }
  }

  
  public String patch_toText(List<Patch> patches) {
    StringBuilder text = new StringBuilder();
    for (Patch aPatch : patches) {
      text.append(aPatch);
    }
    return text.toString();
  }

  
  public List<Patch> patch_fromText(String textline)
      throws IllegalArgumentException {
    List<Patch> patches = new LinkedList<Patch>();
    if (textline.length() == 0) {
      return patches;
    }
    List<String> textList = Arrays.asList(textline.split("\n"));
    LinkedList<String> text = new LinkedList<String>(textList);
    Patch patch;
    Pattern patchHeader
        = Pattern.compile("^@@ -(\\d+),?(\\d*) \\+(\\d+),?(\\d*) @@$");
    Matcher m;
    char sign;
    String line;
    while (!text.isEmpty()) {
      m = patchHeader.matcher(text.getFirst());
      if (!m.matches()) {
        throw new IllegalArgumentException(
            "Invalid patch string: " + text.getFirst());
      }
      patch = new Patch();
      patches.add(patch);
      patch.start1 = Integer.parseInt(m.group(1));
      if (m.group(2).length() == 0) {
        patch.start1--;
        patch.length1 = 1;
      } else if (m.group(2).equals("0")) {
        patch.length1 = 0;
      } else {
        patch.start1--;
        patch.length1 = Integer.parseInt(m.group(2));
      }

      patch.start2 = Integer.parseInt(m.group(3));
      if (m.group(4).length() == 0) {
        patch.start2--;
        patch.length2 = 1;
      } else if (m.group(4).equals("0")) {
        patch.length2 = 0;
      } else {
        patch.start2--;
        patch.length2 = Integer.parseInt(m.group(4));
      }
      text.removeFirst();

      while (!text.isEmpty()) {
        try {
          sign = text.getFirst().charAt(0);
        } catch (IndexOutOfBoundsException e) {
          
          text.removeFirst();
          continue;
        }
        line = text.getFirst().substring(1);
        line = line.replace("+", "%2B");  
        try {
          line = URLDecoder.decode(line, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          
          throw new Error("This system does not support UTF-8.", e);
        } catch (IllegalArgumentException e) {
          
          throw new IllegalArgumentException(
              "Illegal escape in patch_fromText: " + line, e);
        }
        if (sign == '-') {
          
          patch.diffs.add(new Diff(Operation.DELETE, line));
        } else if (sign == '+') {
          
          patch.diffs.add(new Diff(Operation.INSERT, line));
        } else if (sign == ' ') {
          
          patch.diffs.add(new Diff(Operation.EQUAL, line));
        } else if (sign == '@') {
          
          break;
        } else {
          
          throw new IllegalArgumentException(
              "Invalid patch mode '" + sign + "' in: " + line);
        }
        text.removeFirst();
      }
    }
    return patches;
  }


  
  public static class Diff {
    
    public Operation operation;
    
    public String text;

    
    public Diff(Operation operation, String text) {
      
      this.operation = operation;
      this.text = text;
    }

    
    public String toString() {
      String prettyText = this.text.replace('\n', '\u00b6');
      return "Diff(" + this.operation + ",\"" + prettyText + "\")";
    }

    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = (operation == null) ? 0 : operation.hashCode();
      result += prime * ((text == null) ? 0 : text.hashCode());
      return result;
    }

    
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Diff other = (Diff) obj;
      if (operation != other.operation) {
        return false;
      }
      if (text == null) {
        if (other.text != null) {
          return false;
        }
      } else if (!text.equals(other.text)) {
        return false;
      }
      return true;
    }
  }


  
  public static class Patch {
    public LinkedList<Diff> diffs;
    public int start1;
    public int start2;
    public int length1;
    public int length2;

    
    public Patch() {
      this.diffs = new LinkedList<Diff>();
    }

    
    public String toString() {
      String coords1, coords2;
      if (this.length1 == 0) {
        coords1 = this.start1 + ",0";
      } else if (this.length1 == 1) {
        coords1 = Integer.toString(this.start1 + 1);
      } else {
        coords1 = (this.start1 + 1) + "," + this.length1;
      }
      if (this.length2 == 0) {
        coords2 = this.start2 + ",0";
      } else if (this.length2 == 1) {
        coords2 = Integer.toString(this.start2 + 1);
      } else {
        coords2 = (this.start2 + 1) + "," + this.length2;
      }
      StringBuilder text = new StringBuilder();
      text.append("@@ -").append(coords1).append(" +").append(coords2)
          .append(" @@\n");
      
      for (Diff aDiff : this.diffs) {
        switch (aDiff.operation) {
        case INSERT:
          text.append('+');
          break;
        case DELETE:
          text.append('-');
          break;
        case EQUAL:
          text.append(' ');
          break;
        }
        try {
          text.append(URLEncoder.encode(aDiff.text, "UTF-8").replace('+', ' '))
              .append("\n");
        } catch (UnsupportedEncodingException e) {
          
          throw new Error("This system does not support UTF-8.", e);
        }
      }
      return unescapeForEncodeUriCompatability(text.toString());
    }
  }

  
  private static String unescapeForEncodeUriCompatability(String str) {
    return str.replace("%21", "!").replace("%7E", "~")
        .replace("%27", "'").replace("%28", "(").replace("%29", ")")
        .replace("%3B", ";").replace("%2F", "/").replace("%3F", "?")
        .replace("%3A", ":").replace("%40", "@").replace("%26", "&")
        .replace("%3D", "=").replace("%2B", "+").replace("%24", "$")
        .replace("%2C", ",").replace("%23", "#");
  }
}
