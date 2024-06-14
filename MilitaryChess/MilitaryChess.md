# MilitaryChess

# 程序原理
## logic包

和可视化界面无关的军棋逻辑。

- LogicContext-CrossScreenDataPackage类

连接logic包和ui包的工具。军棋领域的数据，都保存在此，logic包修改这些数据，ui包读取这些数据。

还负责某些数据修改后引起的额外处理，包括：
afterFight()：一次战斗后，行棋方变化，ChessState（待选择起始棋子、待选择目标棋子、待确认）重置。
update()：行棋方变化后，若当前是Ai方则生成一次Ai动作；行棋方变化后，若是暗棋则修改暗棋范围。
findAtPos()：棋局变化后，按最新状态，通过位置查找棋子。

- AiLogic类

主要方法是generateAiAction()，作用是提供Ai的一步棋。实现方法是遍历每个Ai方棋子的一步的
每个可移动目的地，评估目的地的得分，选出得分最高的目的地。 这段代码的主要功能是生成AI动作，其中包括最佳的移动策略。
代码中的 generateAiAction 方法接收了 当前己方军队的数据、对方军队的数据以及屏幕数据作为参数。
代码首先创建了一个 allPosOfOtherArmy 的集合，用于存储对方军队的所有位置。 然后，通过遍历己方每个棋子，
找到每个棋子的可移动位置， 并计算每个移动位置的得分。 对于可移动终点是敌方棋子的情况，根据吃子等级的不同，
给予不同的得分。吃子等级越高，得分越高。 对于可移动终点是空地的情况，根据该位置对于所有敌军的得分
以及距离的加权平均来计算得分。距离越近，得分越高。最后，遍历所有得分，选择得分最高的From和To作为最佳移动策略，
并返回包含最佳移动策略的AiAction对象。如果找不到最佳移动策略，则返回一个包含capitulated 字段为true的AiAction对象，表示投降。
- ChessRule类

作用管理行走和战斗规则。包括：
canMove(): 不能重叠自己的棋子；某些棋子不可移动；不能从大本营移出；不能移入非空行营
getFightResult(): 给出两个棋子交战结果是发起者胜、发起者败、同尽。
将空地也视为棋子，两个棋子“交战结果”还包括了不合法（!canMove()）、移动（目标棋子是空地）。

- GameboardPosRule类
作用管理和棋盘位置有关的规则。
XING_YING_POS_MAP和DA_BEN_YING_POS_MAP，它们表示游戏板上的特定位置。
- 它声明了两个公共静态地图，simplePosMap和gameboardPosMap，用于存储游戏板上的位置。
- 在一个静态块中，它用游戏板上所有可能的位置初始化simplePosMap和gameboardPosMap。
- 定义findSimplePos方法是为了使用给定的行和列从simplePosMap中查找SimplePos对象。
- 定义simplePosMapKey方法是为了使用给定的行和列为simplePosMap生成一个键。
- buildGameboardNeighbour方法的定义是为给定位置及其在游戏板上的类型构建相邻位置的地图。
- buildGameboardSimplePos方法被定义为为一个给定的SimplePos对象建立一个GameboardPos对象。
- 方向枚举被定义为表示游戏板上的方向。
- finaallmoveccandidates方法被定义为查找给定棋子的所有可能的移动目的地。
- findrailmoveccandidates方法被定义为查找给定棋子在轨道上的所有可能的移动目的地。
- GameboardPos类被定义为表示游戏板上的一个位置，它包括一个SimplePos对象、邻近位置的地图和位置类型。
- GameboardPosType枚举被定义为表示游戏板上的位置类型。
- SimplePos类被定义为表示游戏板上的一个简单位置，它包括一行和一列。

SimplePos类：仅代表一组row和col，和军棋无关。
GameboardPos类：代表一个军棋棋盘上的位置，每个位置属于以下类型之一：铁路、兵站、行营、大本营
Map<Integer, SimplePos> simplePosMap：军棋棋盘的所有row和col。
Map<SimplePos, GameboardPos> gameboardPosMap：每个row和col对应的更多信息的映射关系。
属于和“对局”无关的静态类：不论是否在对局，不论什么状态的对局，这些关系都是静态且不变的。

- ArmyRuntimeData类

和“对局”有关的类，对应一场对局中的一方。管理这一方的所有棋子。

- ChessRuntimeData类

和“对局”有关的类，对应一场对局中的一个棋子。随着棋局进行，其中数据会变化，例如位置变化、类型变化（死亡时变为空地）。
这段代码定义了一个名为ChessRuntimeData的类。它包含了一些关于棋子的运行时数据，如id、位置、UI坐标、棋子类型和棋子方。
其中的方法包括：
- toText(): 将棋子类型和位置转换为文本形式。
- updateUiPos(): 根据布局常量更新棋子的UI坐标。
  还定义了一个枚举类型ChessSide，表示棋子的方，有红方、蓝方和空方三种。
  另外还有一个静态方法fromCodes()，用于根据输入的代码字符串、布局常量和棋子方创建一组棋子运行时数据对象。
  这段代码是为了处理棋子的运行时数据和相关操作。

## ui包（简略描述一下）

可视化有关的代码。可视化方案基于游戏引擎libgdx。官方手册：https://libgdx.com/wiki/

- MilitaryChessGame类

继承libgdx的ManagedGame类，是libgdx要求的游戏主体。

- XXXScreen类

继承libgdx的Screen类，对应一个可视化窗体。

MyMenuScreen：菜单窗体
PrepareScreen：游戏准备窗体
PlayScreen：游戏进行窗体

- CameraDataPackage类

libgdx提供Camera类，用于移动、拉远拉近显示出的图像。因为图形化窗口的大小是固定的，棋盘大小可以是任意数值。通过UI库的相机拉远拉进功能，让棋盘映射到UI上。

本程序想要修改相机参数时，并不是直接修改Camera类，而是先修改CameraDataPackage，再在统一的地方将CameraDataPackage修改到Camera类。更加方便理清逻辑。

- ChessVM类

一个棋子的可视化实现。每个ChessVM对应一个ChessRuntimeData，从ChessRuntimeData中读取棋子位置、类型，然后展示在libgdx提供的控件上（Label展示文本，Image展示图像）。

DeskClickListener类是它的点击事件监听器，注册到libgdx里，即可得到监听关系。

- AllButtonPageVM类

玩家的操作面板，拥有若干按钮。随着下棋状态ChessState不同而变化展示内容。

- PlayScreen类

为了方便管理，将主要的可视化相关的函数都放在本类。

onDeskClicked()：当棋子被点击。这将推进ChessState。

onCommitButtonClicked()：当确认被点击。这将调用ChessRule执行一次战斗，然后对战斗结果进行后续处理。

onClearButtonClicked()：当清空被点击。这将回退ChessState。

onLogicFrame()：已预先注册到libgdx里，使libgdx每秒调用一次。此时若是Ai在执棋，则模拟Ai操作一次（点击起始棋子、点击目标棋子、确认）。
