<script setup>
import { ref, computed, onMounted } from 'vue'
import { AppstoreOutlined, TeamOutlined, PlusOutlined, ArrowRightOutlined, ReloadOutlined, SearchOutlined, SafetyCertificateOutlined, ThunderboltOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import SalesHistory from './SalesHistory.vue'

const page = ref('leads')
const leads = ref([])
const customers = ref([])
const loading = ref(false)
const leadsPage = ref(1)
const leadsTotal = ref(0)
const pageSize = 20
const search = ref('')
const showLead = ref(false)
const showCustomer = ref(false)
const showConvert = ref(false)
const selectedLead = ref(null)
const selectedCustomer = ref(null)
const view360 = ref(null)
const leadName = ref('')
const leadCustomerId = ref(undefined)
const idNumber = ref('')
const conversion = ref({ title: '', operator: '' })
const notice = ref('')

const filteredLeads = computed(() => leads.value.filter(item => item.name.toLowerCase().includes(search.value.toLowerCase())))
const openLeads = computed(() => filteredLeads.value.filter(item => item.status === 'NEW'))
const convertedLeads = computed(() => filteredLeads.value.filter(item => item.status === 'CONVERTED'))
const totalConverted = computed(() => leads.value.filter(item => item.status === 'CONVERTED').length)
const rate = computed(() => leads.value.length ? Math.round(totalConverted.value * 100 / leads.value.length) : 0)

async function api(path, options = {}) {
  const response = await fetch('/api' + path, { headers: { 'Content-Type': 'application/json' }, ...options })
  const data = response.status === 204 ? null : await response.json().catch(() => null)
  if (!response.ok) throw new Error(data?.message || `请求失败 (${response.status})`)
  return data
}
async function refresh() {
  loading.value = true; notice.value = ''
  try {
    const [leadData, customerData] = await Promise.all([api(`/leads?page=${leadsPage.value - 1}&size=${pageSize}`), api('/customers')])
    leads.value = leadData.content; leadsTotal.value = leadData.totalElements; customers.value = customerData
  } catch (error) { notice.value = error.message }
  finally { loading.value = false }
}
async function createLead() {
  if (!leadName.value.trim()) return message.warning('请输入线索名称')
  try { await api('/leads', { method: 'POST', body: JSON.stringify({ name: leadName.value.trim(), customerId: leadCustomerId.value ?? null }) }); leadName.value = ''; leadCustomerId.value = undefined; showLead.value = false; leadsPage.value = 1; message.success('线索已创建'); await refresh() }
  catch (error) { message.error(error.message) }
}
async function changePage(pageNumber) { leadsPage.value = pageNumber; await refresh() }
function openConvert(lead) { selectedLead.value = lead; conversion.value = { title: `${lead.name}的商机`, operator: '' }; showConvert.value = true }
async function convertLead() {
  if (!conversion.value.title.trim() || !conversion.value.operator.trim()) return message.warning('请填写商机名称和操作人')
  try { await api(`/leads/${selectedLead.value.id}/convert`, { method: 'POST', body: JSON.stringify(conversion.value) }); showConvert.value = false; message.success('已转化为商机'); await refresh() }
  catch (error) { message.error(error.message); await refresh() }
}
async function createCustomer() {
  if (!idNumber.value.trim()) return message.warning('请输入身份证号')
  try { await api('/customers', { method: 'POST', body: JSON.stringify({ idNumber: idNumber.value.trim() }) }); idNumber.value = ''; showCustomer.value = false; message.success('客户记录已创建'); await refresh() }
  catch (error) { message.error(error.message) }
}
async function showProfile(customer) {
  selectedCustomer.value = customer; view360.value = null
  try { view360.value = await api(`/customers/${customer.id}/360`) }
  catch (error) { message.error(`客户中心暂不可用：${error.message}`) }
}
function initials(name) { return (name || '?').slice(0, 1).toUpperCase() }
function formatDate(value) { return value ? new Date(value).toLocaleString('zh-CN', { dateStyle: 'medium', timeStyle: 'short' }) : '等待转化' }
onMounted(refresh)
</script>

<template>
  <a-config-provider><div class="app-shell">
    <aside class="sidebar">
      <div class="brand"><div class="brand-mark">青</div><div><strong>青屿 CRM</strong><small>客户经营工作台</small></div></div>
      <div class="nav-label">工作空间</div>
      <button :class="['nav-item', { active: page === 'leads' }]" @click="page = 'leads'; search = ''"><AppstoreOutlined /><span>线索看板</span><span class="nav-count">{{ leads.length }}</span></button>
      <button :class="['nav-item', { active: page === 'customers' }]" @click="page = 'customers'; search = ''"><TeamOutlined /><span>客户视图</span></button>
      <div class="side-bottom"><div class="side-icon"><SafetyCertificateOutlined /></div><div><strong>安全的客户经营</strong><p>让每一次跟进<br>都有迹可循。</p></div></div>
      <div class="side-footer"><span class="avatar">实</span><span><b>实训工作空间</b><small>本地演示环境</small></span><span class="online-dot"></span></div>
    </aside>
    <main class="main-area">
      <header class="topbar"><div class="crumb">工作空间 <span>/</span> <b>{{ page === 'leads' ? '线索看板' : '客户视图' }}</b></div><div class="top-right"><span class="status-dot"></span> 本地工作区 <span class="top-separator"></span><span class="avatar small">实</span></div></header>
      <section class="content">
        <div class="page-heading"><div><div class="eyebrow"><span></span> CUSTOMER RELATIONSHIP MANAGEMENT</div><h1>{{ page === 'leads' ? '线索看板' : '客户视图' }}<span class="heading-dot">.</span></h1><p>{{ page === 'leads' ? '从第一次接触开始，让每一条机会都有方向。' : '连接客户中心与跟进记录，了解客户的完整信息。' }}</p></div><a-button v-if="page === 'leads'" type="primary" size="large" @click="showLead = true"><template #icon><PlusOutlined /></template>新增线索</a-button><a-button v-else type="primary" size="large" @click="showCustomer = true"><template #icon><PlusOutlined /></template>新增客户</a-button></div>
        <a-alert v-if="notice" type="error" show-icon :message="`连接后端失败：${notice}`" description="请确认绿地 MySQL 服务已在 8083 端口启动，然后点击刷新。" class="alert" />
        <template v-if="page === 'leads'">
          <div class="metrics"><div class="metric"><div class="metric-title">全部线索 <span>↗</span></div><div class="metric-value">{{ leadsTotal.toString().padStart(2, '0') }}</div><div class="metric-note"><span class="metric-dot"></span>数据库中的线索总数</div></div><div class="metric"><div class="metric-title">本页待跟进 <span>↗</span></div><div class="metric-value">{{ (leads.length - totalConverted).toString().padStart(2, '0') }}</div><div class="metric-note">等待下一次行动</div></div><div class="metric"><div class="metric-title">本页已转化 <span>↗</span></div><div class="metric-value">{{ totalConverted.toString().padStart(2, '0') }}</div><div class="metric-note">已建立商机</div></div><div class="metric accent-metric"><div class="metric-title">本页转化率 <span>↗</span></div><div class="metric-value">{{ rate }}<em>%</em></div><div class="metric-note">本页已转化 / 本页线索</div></div></div>
          <div class="board-section"><div class="section-top"><div><h2>线索进展 <span>Lead pipeline</span></h2><p>掌握每一个阶段，及时推进潜在机会</p></div><div class="controls"><a-input v-model:value="search" placeholder="搜索本页线索" allow-clear><template #prefix><SearchOutlined /></template></a-input><a-button :loading="loading" @click="refresh"><template #icon><ReloadOutlined /></template>刷新</a-button></div></div>
            <div class="board"><div class="column"><div class="column-title"><div><span class="column-dot amber"></span>待跟进 <span class="pill">{{ openLeads.length }}</span></div><span class="column-menu">···</span></div><p class="column-sub">新的线索，从这里开始</p><div v-if="!openLeads.length" class="empty-card"><div class="empty-icon">＋</div><b>暂无线索</b><p>新增线索，开始第一次跟进</p></div><article v-for="lead in openLeads" :key="lead.id" class="lead-card"><div class="card-top"><span class="lead-avatar">{{ initials(lead.name) }}</span><span class="lead-id">#{{ lead.id.toString().padStart(4,'0') }}</span><span class="card-menu">···</span></div><h3>{{ lead.name }}</h3><p class="card-desc">新的潜在客户机会</p><div class="card-divider"></div><div class="card-meta"><span><span class="mini-dot amber"></span>待跟进</span><button @click="openConvert(lead)">转化商机 <ArrowRightOutlined /></button></div></article><button class="add-card" @click="showLead = true"><PlusOutlined /> 添加线索</button></div>
            <div class="column"><div class="column-title"><div><span class="column-dot green"></span>已转化 <span class="pill">{{ convertedLeads.length }}</span></div><span class="column-menu">···</span></div><p class="column-sub">已成功转化为商机</p><div v-if="!convertedLeads.length" class="empty-card"><div class="empty-icon success">✓</div><b>还没有转化记录</b><p>转化后的线索会出现在这里</p></div><article v-for="lead in convertedLeads" :key="lead.id" class="lead-card"><div class="card-top"><span class="lead-avatar mint">{{ initials(lead.name) }}</span><span class="lead-id">#{{ lead.id.toString().padStart(4,'0') }}</span><span class="card-menu">···</span></div><h3>{{ lead.name }}</h3><p class="card-desc">操作人：{{ lead.convertedBy }}</p><div class="card-divider"></div><div class="card-meta"><span><span class="mini-dot green"></span>已转化</span><span>{{ formatDate(lead.convertedAt) }}</span></div></article></div>
            <div class="insight"><div class="insight-icon"><ThunderboltOutlined /></div><div class="insight-label">WORK SMARTER</div><h3>每一次联系，<br>都是新的开始。</h3><p>及时记录和转化线索，<br>让有价值的机会不被遗漏。</p><div class="insight-line"></div><div class="insight-footer">保持跟进 <span>↗</span></div></div></div><div class="pagination"><a-pagination :current="leadsPage" :page-size="pageSize" :total="leadsTotal" :show-size-changer="false" size="small" @change="changePage" /></div></div>
        </template>
        <template v-else><div class="customer-layout"><div class="customer-list"><div class="section-top customer-head"><div><h2>客户列表 <span>Customers</span></h2><p>通过身份证号关联客户中心</p></div><a-button :loading="loading" @click="refresh"><template #icon><ReloadOutlined /></template>刷新</a-button></div><div v-if="!customers.length" class="customer-empty">暂无客户记录，点击右上角新增。</div><button v-for="customer in customers" :key="customer.id" :class="['customer-row', { selected: selectedCustomer?.id === customer.id }]" @click="showProfile(customer)"><span class="lead-avatar mint">客</span><span class="customer-text"><strong>客户 #{{ customer.id }}</strong><small>身份证号 · {{ customer.idNumber }}</small></span><ArrowRightOutlined /></button></div><div class="profile"><div v-if="!selectedCustomer" class="profile-empty"><div class="profile-symbol"><TeamOutlined /></div><h2>选择一位客户</h2><p>在左侧选择客户，查看客户中心与跟进信息。</p></div><div v-else-if="!view360" class="profile-empty"><div class="profile-symbol"><TeamOutlined /></div><h2>客户 #{{ selectedCustomer.id }}</h2><p>正在获取客户中心数据，或服务暂不可用。</p><a-button @click="showProfile(selectedCustomer)">重新获取</a-button></div><div v-else><div class="profile-top"><span class="lead-avatar large">{{ initials(view360.master.name) }}</span><div><div class="eyebrow">CUSTOMER OVERVIEW</div><h2>{{ view360.master.name }}</h2><p>身份证号 · {{ view360.master.idNumber }}</p></div><span class="vip">{{ view360.master.memberLevel }}</span></div><div class="profile-stats"><div><small>账户状态</small><strong>{{ view360.master.accountStatus }}</strong></div><div><small>可用余额</small><strong>¥ {{ Number(view360.master.availableBalance).toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</strong></div><div><small>持仓数量</small><strong>{{ view360.master.holdings.length }}</strong></div></div><div class="profile-section"><h3>持有产品 <span>HOLDINGS</span></h3><div v-if="!view360.master.holdings.length" class="muted">暂无持仓</div><div v-for="item in view360.master.holdings" :key="item.productName" class="holding"><span>{{ item.productName }}</span><strong>¥ {{ Number(item.value).toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</strong></div></div><SalesHistory :data="view360" /><div class="profile-section"><h3>跟进记录 <span>NOTES</span></h3><div v-if="!view360.notes.content.length" class="muted">暂无跟进记录</div><div v-for="note in view360.notes.content" :key="note.id" class="holding"><span>{{ note.content }}</span><small>{{ note.category }}</small></div></div></div></div></div></template>
        <footer><span>© 2026 青屿 CRM · 客户经营工作台</span><span>让关系，创造更多价值 <b>✳</b></span></footer>
      </section>
    </main>
    <a-modal v-model:open="showLead" title="新增线索" ok-text="创建线索" cancel-text="取消" @ok="createLead"><label class="form-label" for="lead-name">线索名称</label><a-input id="lead-name" v-model:value="leadName" placeholder="例如：张先生 / 某某公司" @press-enter="createLead" /><label class="form-label">关联客户（可选）</label><a-select v-model:value="leadCustomerId" allow-clear placeholder="选择已有客户" style="width:100%" :options="customers.map(c => ({ value: c.id, label: `客户 #${c.id} · ${c.idNumber}` }))" /></a-modal>
    <a-modal v-model:open="showConvert" title="转化为商机" ok-text="确认转化" cancel-text="取消" @ok="convertLead"><p class="modal-intro">转化后将创建商机，并记录操作人和转化时间。</p><label class="form-label" for="opp-title">商机名称</label><a-input id="opp-title" v-model:value="conversion.title" placeholder="请输入商机名称" /><label class="form-label" for="operator">操作人</label><a-input id="operator" v-model:value="conversion.operator" placeholder="请输入操作人姓名" /></a-modal>
    <a-modal v-model:open="showCustomer" title="新增客户记录" ok-text="创建客户" cancel-text="取消" @ok="createCustomer"><p class="modal-intro">通过身份证号关联客户中心，姓名及金融信息由客户中心提供。</p><label class="form-label" for="identity">身份证号</label><a-input id="identity" v-model:value="idNumber" placeholder="请输入身份证号" @press-enter="createCustomer" /></a-modal>
  </div></a-config-provider>
</template>

