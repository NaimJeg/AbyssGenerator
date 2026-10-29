Abyss Generator

面向 Minecraft 26.1.2 与 NeoForge 的通用三维体素区块项目。

目标是让主世界、下界、末地及模组维度共用按 X/Y/Z 加载、生成、模拟、保存和同步的 section 系统。
专用维度仅用于测试，不是产品范围。首版只支持新存档，各维度保留自己的高度范围。
最终要求任意生成器透明接入且按 section 生成；该能力尚未实现，也尚未证明对任意第三方生成器可行。
整列生成后拆分不视为完成此目标。

开发基线、架构、阶段任务与验收步骤见 [三维区块实施方案](docs/implementation-plan.md)。

### 门格海绵世界生成

创建新世界时，选择世界类型 **「深渊 — 门格海绵立方体」**（英文：
**Abyss — Menger Cubes**）。专用服务器的新存档可设置：

```properties
level-type=abyssgenerator:fractal
```

- 生成器 ID 为 `abyssgenerator:fractal`，世界预设 ID 也为 `abyssgenerator:fractal`。
- 用边长 **48 格（3 个 section 宽度）**的三级门格海绵，沿 X/Y/Z 平铺。
  由石头和空气构成，保留三个方向贯穿的孔洞；形状不随种子变化。
- 世界高度为 **[-2032, 2032)**。无天空光，初始出生点为天然平台上的 `(24,16,0)`。
- 新建普通世界的主世界保持原版；新建世界的 `abyssgenerator:abyss` 维度也使用此生成器。
  选择分形世界预设时，主世界使用分形地形，下界和末地保持原版。
- 请用新存档体验；不会迁移或重新生成旧存档中的区域。

该功能使用原版区块生成、保存和同步流程，尚不是按 Y 按需加载。
分形生成器已完成 [AutoJS 验收](docs/acceptance/fractal-worldgen.md)。

生产生成层新增 `SectionTerrainGenerator` 单 section 接口，门格海绵的整列入口与单 section 入口共用实现。
`SectionGenerationSupport` 区分原生 section 生成与待适配的整列生成器；不提供隐式整列回退。
这次重构通过现有编译、单元测试及构建，尚未重新进行游戏内验收；上述 AutoJS 记录对应重构前版本。

### 三维按需加载原型

Implementation status: baseline tools and an opt-in P1 server bridge are present.
**This is not the playable three-dimensional chunk mod yet.** The P1 gate fails
on unknown reads and vanilla column packet ownership; P2–P7 are not implemented.
See the [acceptance record](docs/acceptance/p0-p1.md) for measured results and limitations.

P1 新增有超时、去重、取消和关闭保护的异步管理准备入口，以及按世界隔离的生成阶段审计。
它仍依赖原版列加载；`readySection` 只提供显式就绪查询，尚未解决原版未知读取或整列包覆盖。
`wideytest prepare <y>` 报告请求已提交，完成结果会另行输出；它不是按 section 地形生成。

运行 `.\scripts\acceptance\Run-Bridge.ps1` 可复现桥接、异步准备和生成差异实验。
脚本使用新隔离存档，正常停止自启服务器并恢复原服务器配置；最后的 P1 门槛目前仍应失败，
不能把前面子项通过当成完整三维功能通过。原始结果保存在 `build/acceptance/results/bridge-<时间>/`。

生成接入新增按 section 执行的地表规则计算，保留跨边界石层、水面、原始高度及生物群系扩展上下文。
无结构的独立噪声边界采样已在两种种子、原版三维度和特殊地表夹具中与整列对照一致；
这仍是生成阶段组件，尚未接管正常世界生成。结构、雕刻、装饰和完整 P1 运行时接入尚未完成，
边界采样也尚未通过 P7 的工作量门槛。

Build and run the isolated AutoJS baseline suite on Windows:

```powershell
.\gradlew.bat compileJava compileTestJava test build --console=plain
.\scripts\acceptance\Run-Baseline.ps1
```

The runner creates a new fixed-seed test world under `build/acceptance/server`,
performs two real server restarts, and writes results under
`build/acceptance/results/baseline-run`. It leaves existing development worlds alone.
AutoJS and Rhino are development dependencies. Optional KubeJS can be enabled
with `-PwithKubeJS=true`, but the pinned KubeJS build currently fails dedicated
server startup with a missing `BATIcons` class.

`runBridgeServer` enables the experimental sparse bridge for the development-only
`abyssgenerator:bridge_fixture` dimension in its own directory
and exposes AutoJS on port 25588. The bridge is disabled in normal client/server
runs and rejects dimension travel while enabled. It is an administrative test
fixture, not a supported world format or gameplay mode.
