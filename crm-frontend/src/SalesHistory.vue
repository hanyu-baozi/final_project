<script setup>
defineProps({ data: { type: Object, required: true } })
</script>
<template>
  <div class="profile-section">
    <h3>销售记录 <span>SALES</span></h3>
    <div class="holding"><span>线索</span><strong>{{ data.leads.length }} 条</strong></div>
    <div class="holding"><span>商机</span><strong>{{ data.opportunities.length }} 条</strong></div>
    <div class="holding"><span>订单</span><strong>{{ data.orders.length }} 笔</strong></div>
    <div v-for="order in data.orders" :key="order.id" class="holding">
      <span>{{ order.orderNo }}</span><strong>¥ {{ Number(order.amount).toFixed(2) }}</strong>
    </div>
  </div>
</template>
