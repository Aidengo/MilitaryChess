package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPosType;
import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPos;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import lombok.Getter;

/**
 * 行走和战斗规则
 */
public class ChessRule {


    public static boolean canMove(ChessRuntimeData from, ChessRuntimeData to) {
        // 不能重叠自己的棋子
        if (from.getChessSide() == to.getChessSide()) {
            return false;
        }
        // 某些ChessType不可移动
        if (!from.getChessType().isCanMove()) {
            return false;
        }
        GameboardPos fromGameboardPos = GameboardPosRule.gameboardPosMap.get(from.getPos());
        GameboardPos toGameboardPos = GameboardPosRule.gameboardPosMap.get(to.getPos());
        // 不能从大本营移出
        if (fromGameboardPos.getGameboardPosType() == GameboardPosType.DA_BEN_YING) {
            return false;
        }
        // 不能移入非空行营
        if (toGameboardPos.getGameboardPosType() == GameboardPosType.XING_YING && to.getChessSide() != ChessSide.EMPTY) {
            return false;
        }
        return true;
    }

    public static FightResultType fightResultPreview(ChessRuntimeData from, ChessRuntimeData to) {
        if (!canMove(from, to)) {
            return FightResultType.CAN_NOT;
        }
        return getFightResult(from, to);
    }

    @Getter
    public enum FightResultType {
        CAN_NOT("不合法"),
        JUST_MOVE("移动"),
        FROM_WIN("发起者胜"),
        TO_WIN("发起者败"),
        BOTH_DIE("同尽"),
        ;
        final String chinese;
        FightResultType(String chinese){
            this.chinese = chinese;
        }

    }

    public static void fight(ChessRuntimeData from, ChessRuntimeData to) {
        FightResultType fightResultType = getFightResult(from, to);
        if (fightResultType == FightResultType.BOTH_DIE || fightResultType == FightResultType.TO_WIN) {
            setAsDead(from);
        }
        if (fightResultType == FightResultType.BOTH_DIE || fightResultType == FightResultType.FROM_WIN) {
            setAsDead(to);
        }
        if (fightResultType == FightResultType.FROM_WIN || fightResultType == FightResultType.JUST_MOVE) {
            switchPos(from, to);
        }
    }

    /**
     * 死亡即变成空地
     */
    private static void setAsDead(ChessRuntimeData target) {
        target.setChessSide(ChessSide.EMPTY);
        target.setChessType(ChessType.EMPTY);
    }

    /**
     * 交换位置。和空地交换位置即为移动。
     */
    private static void switchPos(ChessRuntimeData from, ChessRuntimeData to) {
        var temp = from.getPos();
        from.setPos(to.getPos());
        to.setPos(temp);
    }

    private static FightResultType getFightResult(ChessRuntimeData from, ChessRuntimeData to) {

        if ((from.getChessType() == ChessType.ZHA_DAN)
            &&(to.getChessType() == ChessType.SI_LING||to.getChessType() == ChessType.JUN_ZHANG
            ||to.getChessType() == ChessType.SHI_ZHANG||to.getChessType() == ChessType.ZHA_DAN
            ||to.getChessType() == ChessType.LV_ZHANG||to.getChessType() == ChessType.TUAN_ZHANG
            ||to.getChessType() == ChessType.YING_ZHANG||to.getChessType() == ChessType.LIAN_ZHANG
            ||to.getChessType() == ChessType.PAI_ZHANG||to.getChessType() == ChessType.GONG_BING
            ||to.getChessType() == ChessType.DI_LEI||to.getChessType() == ChessType.JUN_QI)) {
            return FightResultType.BOTH_DIE;
        }
        if ((to.getChessType() == ChessType.ZHA_DAN)
            &&(from.getChessType() == ChessType.LV_ZHANG||from.getChessType() == ChessType.TUAN_ZHANG
            ||from.getChessType() == ChessType.SI_LING||from.getChessType() == ChessType.JUN_ZHANG
            ||from.getChessType() == ChessType.SHI_ZHANG||from.getChessType() == ChessType.ZHA_DAN
            ||from.getChessType() == ChessType.YING_ZHANG||from.getChessType() == ChessType.LIAN_ZHANG
            ||from.getChessType() == ChessType.PAI_ZHANG||from.getChessType() == ChessType.GONG_BING
            ||from.getChessType() == ChessType.DI_LEI||from.getChessType() == ChessType.JUN_QI)) {
            return FightResultType.BOTH_DIE;
        }


        if (from.getChessType() == ChessType.ZHA_DAN
            &&to.getChessType() == ChessType.EMPTY){
            return FightResultType.JUST_MOVE;
        }

        if (to.getChessType() == ChessType.DI_LEI) {
            if (from.getChessType() == ChessType.GONG_BING) {
                return FightResultType.FROM_WIN;
            } else {
                return FightResultType.TO_WIN;
            }
        }

        if (to.getChessType() == ChessType.EMPTY) {
            return FightResultType.JUST_MOVE;
        }

        int delta = from.getChessType().getCode().charAt(0)
            - to.getChessType().getCode().charAt(0);
        if (delta < 0) {
            return FightResultType.FROM_WIN;
        } else if (delta > 0) {
            return FightResultType.TO_WIN;
        } else {
            return FightResultType.BOTH_DIE;
        }

    }
}
