# 门格海绵生成类型验收

日期：2026-10-29。Minecraft 26.1.2 / NeoForge 26.1.2.124 / Java 25.0.3。

## 实现

注册 `abyssgenerator:fractal` 生成器及同名世界预设。预设列入原版正常世界类型标签，
提供中英文名称；预设主世界及专用 abyss 维度采用模组维度类型，范围为
`[-2032,2032)`，下界和末地沿用原版。普通预设的主世界不替换。

48×48×48 格的单元沿三个坐标轴平铺。单元按三级门格海绵规则去除交叉中心区域，
通过体素中心采样把 27 阶网格映射到 48 格，保留对称性、贯穿孔洞和负坐标连续性。
最大单元边长为三个 section；内部递归结构更小。材质为石头与空气，种子不改变规则，
不附加原版矿物、结构、湖泊、地表装饰或原始生物生成。

生成工作在原版后台执行器上完成，使用每生成器实例最多 27 个只读 palette 模板。
每个安装的 section 获取独立副本，生物群系来自固定的 the_void。
高度图、基础列查询和实际生成调用同一几何规则。

初始出生点 `(24,16,0)` 已有天然石质地板及两格净空，不额外挖掘或铺平台。
真实客户端验收发现原版无天空光出生分支会跳过区块加载，导致冷区块出生错误。
`FractalPlayerSpawnMixin` 仅对本生成器的无天空光世界，在原版碰撞查找前使用
临时 `PLAYER_SPAWN` 票据异步加载目标列；不增加永久强制加载区块。

原三维加载原型移至 test resources 的 `abyssgenerator:bridge_fixture`，分形维度
不启用该原型。生产 JAR 不包含测试维度及命令工具。

## 已执行的检查

| 检查 | 结果 |
| --- | --- |
| 编译、JUnit、build | 26 项测试全部通过 |
| 完整 48³ 单元 | 与独立递归细分参考实现一致，轴置换和反射对称 |
| 负坐标及 section 接缝 | 48 格周期一致；包含整数边界测试 |
| 新存档专用服务器 | `level-type=abyssgenerator:fractal` 成功启动 |
| 实际地形 | 209 个跨高度、跨区块、负坐标采样与几何规则一致 |
| 世界高度边界 | -2032、2031 有效；范围外读取为空气 |
| 高度图、getBaseColumn | 与世界实际方块一致 |
| 两个分形维度 | 主世界和 abyss 都使用专用生成器 |
| 普通世界对照 | 主世界仍是原版噪声生成器，minY=-64、height=384 |
| P1 夹具回归 | 独立 bridge_fixture 的共享空视图、三高度写入及所有权测试通过 |
| 修改隔离 | 修改一个 section 不影响同模板的邻列或另一个维度 |
| 保存及进程重启 | 重启后生成器 codec、地形、出生点与采样全部通过 |
| 创建世界界面 | 正常预设列表包含 `abyssgenerator:fractal`，显示名称正确 |
| 冷区块首次登录 | 先确认出生列已卸载；新身份 FractalCheck 落在 `(24.5,16,0.5)` |
| 客户端方块 | 13 个高度的实际方块及出生点净空通过 |
| 视觉检查 | 实机截图可见三级孔洞、重复立方体及贯穿通道 |
| JAR 检查 | 预设包含在产物中；桥接测试维度和开发命令未打包 |

视觉检查使用旁观者模式及夜视，以便观察无天空光环境。截图未进行图像编辑。
证据位于 `build/acceptance/client/screenshots/fractal-menger.png`。

## 复现

使用新测试存档，配置 `build/acceptance/fractal-server/server.properties`：

```properties
level-name=fractal-test-new
level-type=abyssgenerator:fractal
level-seed=104729
server-ip=127.0.0.1
server-port=25569
online-mode=false
view-distance=3
simulation-distance=2
pause-when-empty-seconds=0
```

```powershell
.\gradlew.bat compileJava compileTestJava test build --console=plain
.\gradlew.bat runFractalServer --console=plain
# 另一个终端；AutoJS 在 25589，游戏服务器在 25569。
.\scripts\acceptance\Invoke-AutoJS.ps1 -Port 25589 -Script scripts/acceptance/fractal.js -TimeoutMs 45000
```

正常停服并重启后再次运行同一脚本，验证持久化后的世界。脚本将测试位置恢复为石头，
不把临时的修改隔离测试留在生成地形中。

客户端使用 `runAcceptanceClient -PacceptanceUser=<全新的测试名>`，AutoJS 端口为
25587。用 `acceptanceAddress='127.0.0.1:25569'` 前缀执行 `connect.js`，等待首次
进入世界后、移动玩家之前执行 `fractal-client.js`。若使用旧玩家数据，其保存的位置
不一定是出生点，不能替代首次登录检查。

原始结果：`build/acceptance/results/fractal-generation.json`、`fractal-restart.json`、
`fractal-world-type-menu.json`、`fractal-client-final.json`。
普通世界及夹具回归记录分别为 `fractal-normal-world-control.json` 和
`fractal-bridge-regression.json`，位于同一结果目录。

## 范围

此功能完成专用世界生成类型，仍使用原版整列加载、持久化、同步及渲染。
它不表示原三维区块实施计划的 P1–P7 已通过，不提供任意高度，也不迁移旧存档。
本轮未做十分钟压力测试或渲染优化兼容验收。
