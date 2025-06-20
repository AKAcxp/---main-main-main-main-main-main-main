新版系统功能模块架构
🟩 1️⃣ 用户账户与安全认证模块
功能：

用户注册、登录

生成和校验 JWT Token

角色权限管理（用户 / 营养师 / 管理员 / AI 实验员）

关键点：

登录后拿到 Token，前端携带 Token 访问所有需要认证的模块

后端的 SecurityConfig + JwtFilter 统一做拦截、鉴权

🟩 2️⃣ 用户个人健康档案模块
功能：

用户填写和更新基本信息（身高、体重、目标、饮食偏好、不吃的食物等）

自动计算 BMI 和健康状态（如偏瘦、正常、超重等）

上下游关系：

提供数据给饮食推荐模块（如：目标是增肌/减脂？BMI 是多少？）

提供用户画像给 AI 聊天助手（让助手回答更个性化）

🟩 3️⃣ 饮食日志模块
功能：

用户记录每日饮食（食物、热量、用餐时间）

后端做摄入量的汇总和分析

上下游关系：

影响健康状态的监测（比如，吃的热量是否超标）

作为 AI 聊天助手的知识库（可以结合聊天帮用户回顾饮食）

🟩 4️⃣ 运动日志模块
功能：

用户记录每日运动（项目、时长、热量消耗、时间）

后端做能量消耗汇总

上下游关系：

运动记录影响健康状态分析和推荐饮食量（热量缺口/盈余）

也可以被 AI 聊天助手调用做分析

🟩 5️⃣ 饮食推荐引擎模块
功能：

根据【个人健康档案】+【用户目标】+【用户饮食偏好】，智能推荐每日食物

生成可直接使用的食谱清单

上下游关系：

使用用户档案模块的数据（身高体重、BMI、目标、口味）

可被前端直接调用（推荐页面）

也可被 AI 聊天助手拿来做推荐（如用户提问“我今天该吃什么？”）

🟩 6️⃣ AI 智能助手模块
功能：

提供对话式问答界面

整合上下游模块的能力（查 BMI、查饮食记录、查推荐、解释营养知识）

上下游关系：

可以调用【饮食推荐】API

可以查询【饮食日志】和【运动日志】API

可以基于【用户健康档案】做个性化回答

让用户的交互体验更流畅、更智能化

🟩 7️⃣ 管理后台 & 系统运维模块
功能：

管理用户、角色、营养师认证

食物数据库管理（手动或 OpenFoodFacts 同步）

系统配置（JWT、AI Key 等）和监控（Actuator）

上下游关系：

保障前台业务稳定运行

可在后台配置 AI 接口密钥、食物库来源等
