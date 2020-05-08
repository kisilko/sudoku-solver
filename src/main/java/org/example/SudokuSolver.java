package org.example;

public class SudokuSolver {

    char[] sudokuBoard;

    int[] preCalculatedRows = new int[81];
    int[] preCalculatedCols = new int[81];
    int[] preCalculatedSquares = new int[81];

    public char[] solve(char[] board) {
        init();
        sudokuBoard = board.clone();
        int[][] unresolvedCoordinates = matchPossibleValues(sudokuBoard);

        if (unresolvedCoordinates.length > 0) {
            leadsToSolution(board.clone(), unresolvedCoordinates, 0);
        }

        return sudokuBoard;
    }

    void init() {
        for (int i = 0; i < 81; i++) {
            preCalculatedRows[i] = i / 9;
            preCalculatedCols[i] = i % 9;
            preCalculatedSquares[i] = (preCalculatedRows[i] / 3) * 3 + preCalculatedCols[i] / 3;
        }
    }

    void leadsToSolution(char[] board, int[][] unresolvedCoordinates, int index) {
        if (index == unresolvedCoordinates.length) {
            if(isBoardValid(board)) {
                sudokuBoard = board.clone();
            }
        } else {
            int[] value = unresolvedCoordinates[index];
            int in = value[0];
            for (int i = 1; i < value.length; i++) {
                if (board[in] != '.') {
                    leadsToSolution(board, unresolvedCoordinates, index + 1);
                    break;
                } else {
                    char[] newBoard = board.clone();
                    newBoard[in] = Character.forDigit(value[i], 10);
                    if (!isBoardValid(board)) continue;
                    checkIfSolvable(newBoard);
                    leadsToSolution(newBoard, unresolvedCoordinates, index + 1);
                }
            }
        }
    }

    boolean checkIfSolvable(char[] board) {
        int matched = -1;
        while(matched != 0) {
            matched = 0;
            for (int i = 0; i < 81; i++) {
                char[] possibleValues = getPossibleValues(board, i);
                if (possibleValues.length == 1) {
                    if (board[i] == '.') {
                        board[i] = possibleValues[0];
                        matched++;
                    }
                }
            }
        }
        return true;
    }

    int[][] matchPossibleValues(char[] board) {
        char[][] possibleValues = new char[81][];
        int numOfUnresolved = 0;
        int matched = -1;
        while(matched != 0) {
            matched = 0;
            numOfUnresolved = 0;
            for (int i = 0; i < 81; i++) {
                possibleValues[i] = getPossibleValues(board, i);
                if (possibleValues[i].length == 1) {
                    if (board[i] == '.') {
                        board[i] = possibleValues[i][0];
                        matched++;
                    }
                } else {
                    numOfUnresolved++;
                }
            }
        }

        int[][] unresolved = new int[numOfUnresolved][];
        for (int i = 0; i < 81; i++) {
            if (possibleValues[i].length > 1) {
                unresolved[--numOfUnresolved] = new int[possibleValues[i].length + 1];
                unresolved[numOfUnresolved][0] = i;
                for (int j = 1; j <= possibleValues[i].length; j++) {
                    unresolved[numOfUnresolved][j] = Character.getNumericValue(possibleValues[i][possibleValues[i].length - j]);
                }
            }
        }
        return unresolved;
    }

    char[] getPossibleValues(char[] board, int i) {
        if (board[i] != '.') return new char[]{board[i]};

        char[] allValues = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        SudokuSet impossibleValues = getImpossibleValues(board, i);
        int numOfImpossibleValues = impossibleValues.getNumOfValuesInSet();
        char[] possibleValues = new char[9 - numOfImpossibleValues];
        int counter = 0;
        for(char c : allValues) {
            if (!impossibleValues.contains(c)) {
                possibleValues[counter++] = c;
            }
        }

        return possibleValues;
    }

    SudokuSet getImpossibleValues(char[] board, int index) {
        int row = preCalculatedRows[index];
        int col = preCalculatedCols[index];
        int square = preCalculatedSquares[index];

        int currentRow, currentCol, currentSquare;
        SudokuSet impossibleValuesSet = new SudokuSet();

        for (int i = 0; i < 81; i++) {
            if (board[i] != '.') {
                currentRow = preCalculatedRows[i];
                currentCol = preCalculatedCols[i];
                currentSquare = preCalculatedSquares[i];
                if (row == currentRow || col == currentCol || square == currentSquare) {
                    impossibleValuesSet.add(board[i]);
                }
            }
        }

        return impossibleValuesSet;
    }

    boolean isBoardValid(char[] board) {
        SudokuSet[] rows = new SudokuSet[9];
        SudokuSet[] cols = new SudokuSet[9];
        SudokuSet[] squares = new SudokuSet[9];

        for (int n = 0; n < 9; n++) {
            rows[n] = new SudokuSet();
            cols[n] = new SudokuSet();
            squares[n] = new SudokuSet();
        }

        for (int i = 0; i < 81; i++) {
            if (board[i] == '.') continue;

            if (!rows[preCalculatedRows[i]].add(board[i]) ||
                    !cols[preCalculatedCols[i]].add(board[i]) ||
                    !squares[preCalculatedSquares[i]].add(board[i])) {
                return false;
            }
        }
        return true;
    }

    char[] toSingleDimension(char[][] board) {
        char [] singleDimension = new char[81];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(board[i], 0, singleDimension, i * 9, 9);
        }
        return singleDimension;
    }


    static class SudokuSet {
        private final int[] set;
        SudokuSet() {
            this.set = new int[10]; // ignore index 0
        }

        boolean add(int el) {
            if (set[el] == 0) {
                set[el] = 1;
                return true;
            }
            return false;
        }

        boolean add(char el) {
            return add(Character.getNumericValue(el));
        }

        int[] getSet() {
            return set;
        }

        boolean contains(char el) {
            return set[Character.getNumericValue(el)] == 1;
        }

        char[] toCharArray() {
            int length = getNumOfValuesInSet();
            char[] charArray = new char[length];
            for (int i = 1; i < 10; i++) {
                if (set[i] != 0) charArray[length--] = Character.forDigit(i, 10);
            }
            return charArray;
        }

        int getNumOfValuesInSet() {
            int num = 0;
            for(int i = 1; i < 10; i++) {
                if (set[i] != 0) num++;
            }
            return num;
        }
    }
}