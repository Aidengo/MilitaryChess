package hundun.militarychess.logic.chess;

import lombok.Getter;


@Getter
public enum ChessType {
    //官方布局：abccddeeffggghhhiiijjjklk
    SI_LING("a", "司令"),
    JUN_ZHANG("b", "军长"),
    SHI_ZHANG("c", "师长"),
    LV_ZHANG("d", "旅长"),
    TUAN_ZHANG("e", "团长"),
    YING_ZHANG("f", "营长"),
    LIAN_ZHANG("g", "连长"),
    PAI_ZHANG("h", "排长"),
    GONG_BING("i", "工兵"),
    DI_LEI("j", "地雷", false),
    ZHA_DAN("k", "炸弹"),
    JUN_QI("l", "军旗", false),
    EMPTY("z", "空", false)
    ;
//bigehkiackdgdfcejgfijljhh    攻守兼备的阵容
    //不可变常量
    final String code;
    final String chinese;
    final boolean canMove;
    ChessType(String code, String chinese, boolean canMove){
        this.code = code;
        this.chinese = chinese;
        this.canMove = canMove;
    }

    ChessType(String code, String chinese){
        this(code, chinese, true);
    }

    public static ChessType fromCode(String code) {
        for (var value : ChessType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new RuntimeException();
    }
}
