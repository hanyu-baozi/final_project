# CRM 后端开发地图

CRM 服务采用 Java 21、Spring Boot 3.2、JPA，单模块按 COLA 包分层。

## 硬规则
- Domain 禁止依赖 App、Adapter 和 Infrastructure；金额使用 BigDecimal。
- 领域状态变化及不变量由实体负责；跨系统访问由 App 编排。
- 每个方法不超过 50 行，核心业务使用中文注释。
- 测试失败不能跳过、删除或注释测试来通过构建。

## 目录
- `crm-backend/src/main/java/edu/crm/adapter`：HTTP 接口。
- `crm-backend/src/main/java/edu/crm/app`：应用流程及事务。
- `crm-backend/src/main/java/edu/crm/domain`：实体、规则及网关。
- `crm-backend/src/main/java/edu/crm/infrastructure`：持久化和外部接口实现。
- `crm-backend/src/test`：H2 集成测试和架构测试。

## 测试
- 正常和异常路径均须有明确断言；关键事务应验证数据库状态。
- Controller 集成测试使用 H2、MockMvc；外部接口依照契约打桩。
- 后端变更运行 `mvn test`，测试和架构检查全部通过才算完成。

## 外部依赖与详细规范
- 客户中心模拟契约：`docs/customer-center-api.yaml`；对接真实系统前必须核对契约。
- 编码与测试细则分别见 `docs/coding-guideline.md`、`docs/test-guideline.md`。
