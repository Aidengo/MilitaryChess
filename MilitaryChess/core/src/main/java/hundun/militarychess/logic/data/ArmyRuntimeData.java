package hundun.militarychess.logic.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArmyRuntimeData {
    List<ChessRuntimeData> chessRuntimeDataList;
}
