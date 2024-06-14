package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.LogicContext.AiAction;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.ChessRule.FightResultType;
import hundun.militarychess.logic.chess.GameboardPosRule.SimplePos;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AiLogic {
    private static int DIE_SCORE = -10;
    private static int killScore(ChessType chessType){
        if (chessType == ChessType.JUN_QI) {
            return 20;
        }
        if (chessType == ChessType.SI_LING) {
            return 18;
        }
        if (chessType == ChessType.JUN_ZHANG) {
            return 16;
        }
        if (chessType == ChessType.SHI_ZHANG) {
            return 14;
        }
        if (chessType == ChessType.LV_ZHANG) {
            return 12;
        }
        if (chessType == ChessType.TUAN_ZHANG) {
            return 10;
        }
        if (chessType == ChessType.YING_ZHANG) {
            return 9;
        }
        if (chessType == ChessType.LIAN_ZHANG) {
            return 8;
        }
        if (chessType == ChessType.PAI_ZHANG) {
            return 6;
        }
        if (chessType == ChessType.GONG_BING) {
            return 4;
        }
//        if ((from.getChessType()==(ChessType.ZHA_DAN))
//        ||/**/) {
//            return 11;
//        }
        return ('z' - chessType.getCode().charAt(0));
    }

    public static AiAction generateAiAction(ArmyRuntimeData fromArmy, ArmyRuntimeData toArmy,
                                            CrossScreenDataPackage crossScreenDataPackage) {
        Set<SimplePos> allPosOfOtherArmy = new HashSet<>();
        toArmy.getChessRuntimeDataList().forEach(it -> allPosOfOtherArmy.add(it.getPos()));

        // 目标是找到得分最高的From和To
        ChessRuntimeData maxScoreFromChess = null;
        SimplePos maxScoreToPos = null;
        int maxScore = -100;
        // 遍历每个我方棋子
        for (ChessRuntimeData checkingFromChess : fromArmy.getChessRuntimeDataList()) {
            Set<SimplePos> all = GameboardPosRule.finaAllMoveCandidates(checkingFromChess, crossScreenDataPackage);
            Map<SimplePos, Integer> scoreMap = new HashMap<>();
            // 遍历每个可移动终点
            for (SimplePos checkingTo : all) {
                ChessRuntimeData checkingToChess = crossScreenDataPackage.findAtPos(checkingTo);
                if (allPosOfOtherArmy.contains(checkingTo)) {
                    // case 可移动终点是敌方棋子。吃子等级越高，得分越高。
                    FightResultType fightResultType = ChessRule.fightResultPreview(checkingFromChess, checkingToChess);
                    if (fightResultType == FightResultType.FROM_WIN) {
                        scoreMap.put(checkingTo, killScore(checkingToChess.getChessType())*4);
                    } else if (fightResultType == FightResultType.BOTH_DIE) {
                        scoreMap.put(checkingTo, killScore(checkingToChess.getChessType())*2);
                    } else if (fightResultType == FightResultType.TO_WIN) {
                        scoreMap.put(checkingTo, DIE_SCORE);
                    }
                } else {
                    // case 可移动终点是空地。这个空地距离的得分，来自该位置对于所有敌军的得分以及距离的加权平均
                    int totalScore = 0;
                    for (ChessRuntimeData it : toArmy.getChessRuntimeDataList()) {
                        FightResultType fightResultType = ChessRule.fightResultPreview(checkingFromChess, it);
                        int distance = Math.abs(it.getPos().getRow() - checkingTo.getRow())
                            + Math.abs(it.getPos().getCol() - checkingTo.getCol());
                        int distanceScore = 21 - distance;
                        int baseKillScore;
                        if (fightResultType == FightResultType.FROM_WIN) {
                            baseKillScore = killScore(checkingToChess.getChessType())*4;
                        } else if (fightResultType == FightResultType.BOTH_DIE) {
                            baseKillScore = killScore(checkingToChess.getChessType())*2;
                        } else if (fightResultType == FightResultType.TO_WIN) {
                            baseKillScore = DIE_SCORE;
                        } else {
                            baseKillScore = 1;
                        }
                        totalScore += distanceScore * baseKillScore / 10;
                    }
                    scoreMap.put(checkingTo, totalScore);
                }
            }
            for (var entry : scoreMap.entrySet()) {
                if (entry.getValue() > maxScore) {
                    maxScore = entry.getValue();
                    maxScoreFromChess = checkingFromChess;
                    maxScoreToPos = entry.getKey();
                }
            }
        }

        if (maxScoreFromChess != null) {
            return AiAction.builder()
                .from(maxScoreFromChess)
                .to(crossScreenDataPackage.findAtPos(maxScoreToPos))
                .score(maxScore).build();
        } else {
            return AiAction.builder().capitulated(true).build();
        }
    }
}
