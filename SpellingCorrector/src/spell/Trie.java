package spell;

public class Trie implements ITrie{
    Node rootNode = new Node();
    int wordCount = 0;
    int nodeCount = 1;
    boolean nodeFound = false;

    @Override
    public void add(String word) {
        String lowCaseWord = word.toLowerCase(); //Convert to lowercase
        addHelper(rootNode, lowCaseWord);
    }

    private void addHelper(Node parentNode, String inputWord) {
        int index = Math.abs('a' - (int) inputWord.charAt(0)); //get index of letter
        //CASE A: One-word letter OR last letter of the word
        if (inputWord.length() == 1) {
            //CASE A1: Node is not found in array
            if (checkNullNode(parentNode, index)) {
                nodeBuilder(parentNode, index);//nodeCount++ and puts node in array
                wordCount++;
            } else{
                if(parentNode.nodes[index].getValue() == 0){
                    wordCount++;
                }
            }
            parentNode.nodes[index].incrementValue();
        }
        //CASE B: 1+ letter-word or NOT last letter of the word
        else{
            //CASE B1: Node is not found in array
            if (checkNullNode(parentNode, index)) {
                nodeBuilder(parentNode, index);
            }
            addHelper(parentNode.nodes[index], inputWord.substring(1));
        }
    }

    public boolean checkNullNode(Node parent, int index){
            return parent.nodes[index] == null;
        }

    public void nodeBuilder(Node parent, int index) {
                parent.nodes[index] = new Node();
                nodeCount++;
            }

    @Override
    public INode find(String word) {
        String lowCaseWord = word.toLowerCase();
        return findHelper(rootNode, lowCaseWord);
    }

    private INode findHelper(Node parentNode, String inputWord){
        int index = Math.abs('a' - (int) inputWord.charAt(0));
        Node referenceNode = new Node();

        //CASE A: One-word letter OR last letter of the word
        if (inputWord.length() == 1) {
            //CASE A1: Node is not found in array
            if (checkNullNode(parentNode, index)) {
                return null;
            }
            //Case A2: Node IS found in the array
            else{
                //Case A2.1: Node has a count greater than zero
                if(parentNode.nodes[index].getValue() > 0){
                    referenceNode = parentNode.nodes[index];
                }
                //Case A2.2: Node has a count of zero
                else{
                    return null;
                }
            }
        }
        //CASE B: Not the last letter to traverse - keeps on going
        else{
            if (checkNullNode(parentNode, index)) {
                return null;
            }
            else{
                referenceNode = (Node) findHelper(parentNode.nodes[index], inputWord.substring(1));
            }
        }
        return referenceNode;
    }

    @Override
    public int getWordCount() { return wordCount; }

    @Override
    public int getNodeCount() { return nodeCount; }


    @Override
    public String toString(){   //Prints list of words
        StringBuilder word = new StringBuilder();
        StringBuilder wordList = new StringBuilder();

        if(rootNode != null){
            toStringHelper(rootNode, word, wordList);
        }
        return wordList.toString();
    }

    private void toStringHelper(Node node, StringBuilder word, StringBuilder words){
        //Case: A word was found
        if(node.count > 0){
            words.append(word.toString());
            words.append('\n');
        }

        for(int i = 0; i < node.nodes.length; i++){
            nodeFound = false;
            //Case: Node is NOT null
            if(!checkNullNode(node, i)){
                nodeFound = true;
                char letter = (char)(i + 97);
                word.append(letter);
                toStringHelper(node.nodes[i], word, words); //Traverse
            }
        }
        //Case: No node found in array
        if(!nodeFound){
            if(word.length() >= 1){
                word.deleteCharAt(word.length() - 1);
            }
        }
    }

    @Override
    public int hashCode(){
        int index = 0;
        for(int i = 0; i < 26; i++){
            if(!checkNullNode(rootNode, i)){
                index = i;
                break;
            }
        }
        return (index * nodeCount * wordCount);
    }

    @Override
    public boolean equals(Object o){
        //Check if object is a trie
        boolean areEqual = true;
        if(o instanceof Trie ){
            Trie newTrie = (Trie) o;
            //CASE: Both Tries are empty
            if(isEmpty(rootNode) && isEmpty(newTrie.rootNode)){
                return true;
            }
            //CASE: This Trie IS empty but New Trie IS NOT
            else if(isEmpty(rootNode) && !isEmpty(newTrie.rootNode)){
                return false;
            }
            //CASE: This Trie IS NOT empty but New Trie IS
            else if(!isEmpty(rootNode) && isEmpty(newTrie.rootNode)){
                return false;
            }
            //CASE: Both are NOT empty
            else if(!isEmpty(rootNode) && !isEmpty(newTrie.rootNode)){
                if(this.getWordCount() == newTrie.getWordCount()){
                    return equalsHelper(rootNode, newTrie.rootNode);
                }
                else{
                    return false;
                }
            }
        }
        else{
            return false;
        }
        return true;
    }

    public boolean equalsHelper(Node nodeA, Node nodeB){
        //Traverse through every index in the Nodes Array
        for(int i = 0; i < 26; i++){
            //CASE: Both are null
            if(checkNullNode(nodeA, i) && checkNullNode(nodeB, i)) {
            }
            //CASE: NodeA IS null BUT nodeB IS NOT
            else if(checkNullNode(nodeA, i) && !checkNullNode(nodeB, i)) {
                return false;
            }
            //CASE: NodeA IS NOT null but NodeB IS
            else if(!checkNullNode(nodeA, i) && checkNullNode(nodeB, i)) {
                return false;
            }
            //CASE: Both are NOT null
            else if(!checkNullNode(nodeA, i) && !checkNullNode(nodeB, i)) {
                if (nodeA.nodes[i].getValue() == nodeB.nodes[i].getValue()) {
                    if(!equalsHelper(nodeA.nodes[i], nodeB.nodes[i])){
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isEmpty(Node node){
        boolean empty = true;
        for(int i = 0; i < 26; i++){
            if(!checkNullNode(node, i)){
                empty = false;
            }
        }
        return empty;
    }
}

