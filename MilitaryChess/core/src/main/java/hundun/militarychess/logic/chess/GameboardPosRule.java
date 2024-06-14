package hundun.militarychess.logic.chess;

import com.badlogic.gdx.utils.Null;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 和棋盘位置有关的规则
 */
public class GameboardPosRule {
    private static final List<SimplePos> XING_YING_POS_MAP = List.of(
        new SimplePos(7, 1),
        new SimplePos(7, 3),
        new SimplePos(8, 2),
        new SimplePos(9, 1),
        new SimplePos(9, 3),
        new SimplePos(2, 1),
        new SimplePos(2, 3),
        new SimplePos(3, 2),
        new SimplePos(4, 1),
        new SimplePos(4, 3)
    );

    private static final List<SimplePos> DA_BEN_YING_POS_MAP = List.of(
        new SimplePos(11, 1),
        new SimplePos(11, 3),
        new SimplePos(0, 1),
        new SimplePos(0, 3)
    );
    public static Map<Integer, SimplePos> simplePosMap;
    public static Map<SimplePos, GameboardPos> gameboardPosMap;
    static {
        simplePosMap = new HashMap<>();
        for (int row = 0; row <= 11; row++) {
            for (int col = 0; col <= 4; col++) {
                simplePosMap.put(simplePosMapKey(row, col), new SimplePos(row, col));
            }
        }
        gameboardPosMap = new HashMap<>();
        simplePosMap.values().forEach(it -> {
            gameboardPosMap.put(it, buildGameboardSimplePos(it));
        });
    }

    private static SimplePos findSimplePos(int row, int col) {
        return simplePosMap.get(simplePosMapKey(row, col));
    }

    private static Integer simplePosMapKey(int row, int col) {
        return row * 100 + col;
    }

    private static void buildGameboardNeighbour(GameboardPos thiz, SimplePos pos, GameboardPosType gameboardPosType) {
        if (pos.getCol() - 1 >= 0) {
            thiz.getNeighbourMap().put(Direction.LEFT, findSimplePos(pos.getRow(), pos.getCol() - 1));
        }
        if (pos.getCol() + 1 <= 4) {
            thiz.getNeighbourMap().put(Direction.RIGHT, findSimplePos(pos.getRow(), pos.getCol() + 1));
        }
        if (pos.getRow() - 1 >= 0) {
            // 特别的，（6,1）和（6,3）不连接UP
            if (!(pos.getRow() == 6 && pos.getCol() == 1) && !(pos.getRow() == 6 && pos.getCol() == 3)) {
                thiz.getNeighbourMap().put(Direction.UP, findSimplePos(pos.getRow() - 1, pos.getCol()));
            }
        }
        if (pos.getRow() + 1 <= 11) {
            // 特别的，（5,1）和（5,3）不连接DOWN
            if (!(pos.getRow() == 5 && pos.getCol() == 1) && !(pos.getRow() == 5 && pos.getCol() == 3)) {
                thiz.getNeighbourMap().put(Direction.DOWN, findSimplePos(pos.getRow() + 1, pos.getCol()));
            }
        }
        // 并且和行营相邻的位置不连接斜向
        int minDistance = XING_YING_POS_MAP.stream()
            .map(it -> Math.abs(it.getRow() - pos.getRow()) + Math.abs(it.getCol() - pos.getCol()))
            .sorted()
            .findFirst()
            .get();
        if (minDistance != 1) {
            // 特别的，1/6/11行不连接LEFT_UP和RIGHT_UP
            if (pos.getRow() - 1 >= 0 && pos.getCol() - 1 >= 0) {
                if (pos.getRow() != 1 && pos.getRow() != 6 && pos.getRow() != 11) {
                    thiz.getNeighbourMap().put(Direction.LEFT_UP, findSimplePos(pos.getRow() - 1, pos.getCol() - 1));
                }
            }
            if (pos.getRow() - 1 >= 0 && pos.getCol() + 1 >= 0) {
                if (pos.getRow() != 1 && pos.getRow() != 6 && pos.getRow() != 11) {
                    thiz.getNeighbourMap().put(Direction.RIGHT_UP, findSimplePos(pos.getRow() - 1, pos.getCol() + 1));
                }
            }
            // 特别的，0/5/10行不连接LEFT_DOWN和RIGHT_DOWN
            if (pos.getRow() + 1 <= 11 && pos.getCol() - 1 >= 0) {
                if (pos.getRow() != 0 && pos.getRow() != 5 && pos.getRow() != 10) {
                    thiz.getNeighbourMap().put(Direction.LEFT_DOWN, findSimplePos(pos.getRow() + 1, pos.getCol() - 1));
                }
            }
            if (pos.getRow() + 1 <= 11 && pos.getCol() + 1 >= 0) {
                if (pos.getRow() != 0 && pos.getRow() != 5 && pos.getRow() != 10) {
                    thiz.getNeighbourMap().put(Direction.RIGHT_DOWN, findSimplePos(pos.getRow() + 1, pos.getCol() + 1));
                }
            }
        }
    }


    private static GameboardPos buildGameboardSimplePos(SimplePos pos) {
        GameboardPos result = GameboardPos.builder()
            .pos(pos)
            .neighbourMap(new HashMap<>())
            .build();
        final int row = pos.getRow();
        final int col = pos.getCol();
        GameboardPosType gameboardPosType;
        if (row <= 0 || row >= 11) {
            if (DA_BEN_YING_POS_MAP.contains(pos)) {
                gameboardPosType = GameboardPosType.DA_BEN_YING;
            } else {
                gameboardPosType = GameboardPosType.BACK_NORMAL;
            }
        } else if (row == 1 || row == 5 || row == 6 || row == 10) {
            gameboardPosType = GameboardPosType.RAIL;
        } else {
            if (col == 0 || col == 4) {
                gameboardPosType = GameboardPosType.RAIL;
            } else {
                if (XING_YING_POS_MAP.contains(pos)) {
                    gameboardPosType = GameboardPosType.XING_YING;
                } else {
                    gameboardPosType = GameboardPosType.FRONT_NORMAL;
                }
            }
        }
        buildGameboardNeighbour(result, pos, gameboardPosType);
        result.setGameboardPosType(gameboardPosType);
        return result;
    }

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        LEFT_UP,
        RIGHT_UP,
        LEFT_DOWN,
        RIGHT_DOWN,
        ;
        public static final List<Direction> XYValues = List.of(
            LEFT,
            RIGHT,
            UP,
            DOWN
        );
        public static Direction getXYOpposite(Direction thiz) {
            switch (thiz) {
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
            }
            return null;
        }
    }

    /**
     * 搜索完整的可移动目的地
     */
    public static Set<SimplePos> finaAllMoveCandidates(
        ChessRuntimeData fromChess,
        CrossScreenDataPackage crossScreenDataPackage
    ) {
        Set<SimplePos> dirtyRailPosList = new HashSet<>();
        Set<SimplePos> result = new HashSet<>();

        SimplePos currentPos = fromChess.getPos();
        boolean canTurnDirection = fromChess.getChessType() == ChessType.GONG_BING;
        GameboardPos currentGameboardPos = GameboardPosRule.gameboardPosMap.get(currentPos);
        // 搜索相邻的可移动目的地
        currentGameboardPos.getNeighbourMap().values().forEach(checkingPos -> {
            ChessRuntimeData checkingChess = crossScreenDataPackage.findAtPos(checkingPos);
            if (checkingChess != null && ChessRule.canMove(fromChess, checkingChess)) {
                result.add(checkingPos);
            }
        });
        if (currentGameboardPos.getGameboardPosType() == GameboardPosType.RAIL) {
            findRailMoveCandidates(fromChess, null, currentPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
        }
        result.remove(currentPos);
        return result;
    }

    /**
     * 搜索铁路上的可移动目的地
     */
    private static void findRailMoveCandidates(
        ChessRuntimeData fromChess,
        @Null Direction currentDirection,
        SimplePos currentPos,
        boolean canTurnDirection,
        CrossScreenDataPackage crossScreenDataPackage,
        Set<SimplePos> result,
        Set<SimplePos> dirtyRailPosList
    ) {
        if (dirtyRailPosList.contains(currentPos)) {
            return;
        }
        result.add(currentPos);
        dirtyRailPosList.add(currentPos);
        GameboardPos currentGameboardPos = GameboardPosRule.gameboardPosMap.get(currentPos);
        // 对于铁路，只尝试4种方向
        for (Direction direction : Direction.XYValues) {
            SimplePos checkingPos = currentGameboardPos.getNeighbourMap().get(direction);
            if (checkingPos == null) {
                continue;
            }
            GameboardPos checkingGameboardPos = GameboardPosRule.gameboardPosMap.get(checkingPos);
            ChessRuntimeData checkingChess = crossScreenDataPackage.findAtPos(checkingPos);
            // checkingPos不是铁路或不可移动则不需继续检查
            if (checkingGameboardPos.getGameboardPosType() != GameboardPosType.RAIL || !ChessRule.canMove(fromChess, checkingChess)) {
                continue;
            }
            // 检查checkingPos是否属于同一条铁路
            if (currentDirection == null || direction == currentDirection || Direction.getXYOpposite(direction) == currentDirection) {
                if (checkingChess.getChessSide() == ChessSide.EMPTY) {
                    // 以它为起点，继续以该方向搜索
                    findRailMoveCandidates(fromChess, direction, checkingPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
                } else {
                    // 它为终点，本方向搜索结束
                    if (!dirtyRailPosList.contains(checkingPos)) {
                        result.add(checkingPos);
                        dirtyRailPosList.add(checkingPos);
                    }
                }
            } else {
                if (canTurnDirection) {
                    if (checkingChess.getChessSide() == ChessSide.EMPTY) {
                        findRailMoveCandidates(fromChess, direction, checkingPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
                    }  else {
                        if (!dirtyRailPosList.contains(checkingPos)) {
                            result.add(checkingPos);
                            dirtyRailPosList.add(checkingPos);
                        }
                    }
                }
            }
        }
    }

    /**
     * 棋盘上的位置。
     * 除了(x, y)，还包含邻居关系，地形。
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GameboardPos {
        SimplePos pos;
        Map<Direction, SimplePos> neighbourMap;
        GameboardPosType gameboardPosType;
    }

    public enum GameboardPosType {
        RAIL,
        FRONT_NORMAL,
        XING_YING,
        BACK_NORMAL,
        DA_BEN_YING,
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimplePos {
        int row;
        int col;
        public String toText() {
            char rowChar = (char)('A' + this.getRow());
            return "(" + rowChar + this.getCol() + ")";
        }
    }
}
