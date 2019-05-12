package chess.parser;

import chess.*;
import chess.piece.Pawn;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGN {
    private static final String PATTERN_PGN = "([VJSDKp]?)([a-h]?)([1-8]?)([x-]?)([a-h][1-8])\\+?=?([VJSD])?([\\+#])?";

    public static Move parseLine(String line, Board board, Piece.Side turn) throws Exception {
        Move move;

        Pattern pgnPattern = Pattern.compile(PATTERN_PGN);
        Matcher pgnMatcher = pgnPattern.matcher(line);

        if (!pgnMatcher.matches()) {
            throw new Exception(line + " Does not match PGN format.");
        }

        String pgnPiece = pgnMatcher.group(1);
        String pgnSourceX = pgnMatcher.group(2);
        String pgnSourceY = pgnMatcher.group(3);
        String pgnDestination = pgnMatcher.group(5);
        String pgnPromotion = pgnMatcher.group(6);

        if (pgnPiece.isEmpty() && !pgnSourceX.isEmpty() && !pgnSourceY.isEmpty()) {
            Position pos = new Position(pgnSourceX.toCharArray()[0] - 'a', Integer.parseInt(pgnSourceY) - 1);
            Piece piece = board.getPiece(pos);
            pgnPiece = piece.getShortName();
        }

        Position dest = new Position(pgnDestination.toCharArray()[0] - 'a', Integer.parseInt(String.valueOf(pgnDestination.toCharArray()[1])) - 1);

        MoveList availableMoves = board.allMoves(turn, true);
        Set<Position> selectedOrigins = new HashSet<>();

        for (Move m : availableMoves) {
            if (!m.getDestination().equals(dest)) {
                // skip moves without desired destination
                continue;
            }

            Position origin = m.getOrigin();
            Piece piece = board.getPiece(origin);

            assert piece != null;

            boolean selected = true;

            if (pgnPiece.isEmpty()) {
                if (!piece.getShortName().equalsIgnoreCase("p")) {
                    selected = false;
                }
            } else {
                if (!piece.getShortName().equalsIgnoreCase(pgnPiece)) {
                    selected = false;
                }
            }
            if (!pgnSourceX.isEmpty() && (pgnSourceX.toCharArray()[0] - 'a') != origin.getX()) {
                selected = false;
            }
            if (!pgnSourceY.isEmpty() && (Integer.parseInt(pgnSourceY) - 1) != origin.getY()) {
                selected = false;
            }
            if (selected) {
                selectedOrigins.add(origin);
            }
        }

        if (selectedOrigins.isEmpty()) {
            throw new Exception("No piece can reach square " + pgnDestination);
        } else if (selectedOrigins.size() > 1) {
            throw new Exception("Several pieces can reach square " + pgnDestination);
        }

        Position src = selectedOrigins.iterator().next();
        move = new Move(src, dest);

        String promotion;
        if (pgnPromotion != null) {
            assert pgnPromotion.length() == 1;
            switch (pgnPromotion) {
                case "D":
                    promotion = "Queen";
                    break;
                case "J":
                    promotion = "Knight";
                    break;
                case "V":
                    promotion = "Rook";
                    break;
                case "S":
                    promotion = "Bishop";
                    break;
                default:
                    promotion = null;
            }
            assert promotion != null;
            move.setNext(new Move(move.getDestination(), null)); // remove pawn
            Move promote = new Move(null, move.getDestination());
            promote.setReplacement(promotion);
            promote.setReplacementSide(turn);
            move.getNext().setNext(promote); // add promotion piece
        }

        return move;
    }

    public static String moveToString(Move move, Board board, Piece.Side turn) throws Exception {
        Piece originP = board.getPiece(move.getOrigin());
        Piece destP = board.getPiece(move.getDestination());

        MoveList availableMoves = board.allMoves(turn, true);

        boolean isCaptured = destP != null;

        if (originP == null) {
            throw new Exception("Illegal move " + move.getOrigin());
        }

        if (originP instanceof Pawn && move.getOrigin().getX() != move.getDestination().getX()) {
            isCaptured = true;
        }

        StringBuilder pgnPart = new StringBuilder();
        if (!(originP instanceof Pawn)) {
            pgnPart.append(originP.getShortName());
        }

        boolean showSourceCol = false;
        boolean showSourceRow = false;
        boolean otherPieceCanReach = false;

        Position origin = move.getOrigin();

        Set<Move> possibleMoves = new HashSet<>();

        for (Move m : availableMoves) {
            if (m.getDestination().equals(move.getDestination())) {
                possibleMoves.add(move);
            }
        }

        possibleMoves.remove(move);

        for (Move m : possibleMoves) {
            otherPieceCanReach = true;

            if (m.getOrigin().getX() == origin.getX()) {
                showSourceCol = true;
            }
            if (m.getOrigin().getY() == origin.getY()) {
                showSourceRow = true;
            }
        }

        boolean isPawnAttack = originP instanceof Pawn && isCaptured;

        if (!showSourceCol && !showSourceRow && (otherPieceCanReach || isPawnAttack)) {
            showSourceCol = true;
        }

        int originRow = 1 + origin.getY();
        char originCol = (char) ('a' + origin.getX());

        if (showSourceCol) {
            pgnPart.append(originCol);
        }
        if (showSourceRow) {
            pgnPart.append(originRow);
        }
        if (isCaptured) {
            pgnPart.append("x");
        }

        String destPos = (char) ('a' + move.getDestination().getX()) + "" + (1 + move.getDestination().getY());
        pgnPart.append(destPos);

        if (originP instanceof Pawn && ((move.getDestination().getY() == 7 && originP.getSide() == Piece.Side.WHITE))
                || (move.getDestination().getY() == 0 && originP.getSide() == Piece.Side.BLACK)) {
            pgnPart.append("=");
            char promotion = 'D';
            switch (move.getNext().getNext().getReplacement()) {
                case "Queen":
                    promotion = 'D';
                    break;
                case "Knight":
                    promotion = 'J';
                    break;
                case "Bishop":
                    promotion = 'S';
                    break;
                case "Rook":
                    promotion = 'V';
                    break;
            }
            pgnPart.append(promotion);
        }

        board.move(move);
        String check = "";
        if (board.check(Piece.opposite(turn))) {
            if (board.checkmate(Piece.opposite(turn))) {
                check = "#";
            }
            else {
                check = "+";
            }
        }
        board.undo();

        pgnPart.append(check);

        return pgnPart.toString();
    }
}
