package spell;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpellCorrector implements ISpellCorrector {
    Trie trie = new Trie();

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        File file = new File(dictionaryFileName);
        Scanner myScanner = new Scanner(file);
        //myScanner.useDelimiter("((#[^\\n]*\\n)|(\\s+))+");

        while (myScanner.hasNext()) {
            String str = myScanner.next();
            trie.add(str);
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        NavigableSet<String> distanceTree1 = new TreeSet<>();
        NavigableSet<String> distanceTree2 = new TreeSet<>();
        int maxCount = 0;
        String maxWord = "none";

        //Distance 1 Tree Setup
        distanceTree1 = getPossibles(inputWord);
        //Distance 2 Tree Setup
        distanceTree1.forEach(newWord -> deletion(newWord, distanceTree2));
        distanceTree1.forEach(newWord -> transposition(newWord, distanceTree2));
        distanceTree1.forEach(newWord -> alteration(newWord, distanceTree2));
        distanceTree1.forEach(newWord -> insertion(newWord, distanceTree2));

        //Traverse Trie looking for each word in Distance 1 Tree
        for (String wordToFind : distanceTree1) {
            if (trie.find(wordToFind) != null) {
                if(wordToFind.equals(inputWord) || wordToFind.equals(inputWord.toLowerCase())){
                    return wordToFind;
                }
                if (trie.find(wordToFind).getValue() > maxCount) {
                    if(!maxWord.equals(inputWord.toLowerCase()) && !maxWord.equals(inputWord)){
                        maxWord = wordToFind;
                        maxCount = trie.find(wordToFind).getValue();
                    }
                }
            }
        }

        //Traverse Trie looking for each word in Distance 2 Tree
        if (maxWord.equals("none")) {
            for (String wordToFind : distanceTree2) {
                if (trie.find(wordToFind) != null) {
                    if (trie.find(wordToFind).getValue() > maxCount) {
                        maxWord = wordToFind;
                        maxCount = trie.find(wordToFind).getValue();
                    }
                }
            }
        }
        if(maxWord.equals("none")){
            return null;
        }
        return maxWord;
    }

    public NavigableSet<String> getPossibles(String word) {
        NavigableSet<String> distanceTree = new TreeSet<>();
        String lowCaseWord = word.toLowerCase();

        deletion(lowCaseWord, distanceTree);
        transposition(lowCaseWord, distanceTree);
        alteration(lowCaseWord, distanceTree);
        insertion(lowCaseWord, distanceTree);

        return distanceTree;
    }

    public void deletion(String word, NavigableSet<String> distanceTree) {
        for (int i = 0; i < word.length(); i++) {
            String newWord = delete(word, i);
            if(!newWord.equals("")){
                distanceTree.add(newWord);
            }
        }
    }

    public void transposition(String word, NavigableSet<String> distanceTree) {
        for (int i = 0; i < word.length(); i++) {
            for (int j = 1; j < word.length(); j++) {
                String newWord = word;
                newWord = swap(newWord, i, j);
                distanceTree.add(newWord);
            }
        }
    }

    public void alteration(String word, NavigableSet<String> distanceTree){
        for(int i = 0; i < word.length(); i++){
            for(int abc = 0; abc < 26; abc++){
                char letter = (char) (abc + 97);
                //Make sure letter is not being replace with same letter
                if(word.charAt(i) != letter){
                    StringBuilder newWord = new StringBuilder(word);
                    newWord.setCharAt(i, letter);
                    distanceTree.add(newWord.toString());
                }
            }
        }
    }

    public void insertion (String word, NavigableSet<String> distanceTree){
        for(int i = 0; i < word.length() + 1; i++) {
            for (int abc = 0; abc < 26; abc++) {
                char letter = (char) (abc + 97);
                String newWord = addChar(word, letter, i);
                distanceTree.add(newWord);
            }
        }
    }

    private static String swap(String str, int i, int j){
        StringBuilder newString  = new StringBuilder(str);
        newString.setCharAt(i, str.charAt(j));
        newString.setCharAt(j, str.charAt(i));
        return newString.toString();
    }

    private String delete(String word, int index){
        StringBuilder newWord = new StringBuilder(word);
        newWord.deleteCharAt(index);
        return newWord.toString();
    }

    private String addChar(String str, char letter, int index){
        StringBuilder newString = new StringBuilder(str);
        newString.insert(index, letter);
        return newString.toString();
    }
}
