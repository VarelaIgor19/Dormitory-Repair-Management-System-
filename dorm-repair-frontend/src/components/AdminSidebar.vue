<template>

<div class="sidebar">

<h2 class="logo">宿舍系统</h2>

<ul>

<li @click="$router.push('/admin/dashboard')">
🏠 首页
</li>

<li>
👤 用户管理
</li>

<li>
🏢 宿舍管理
</li>

<li>
🔧 报修管理
</li>

<li>
📄 申请管理
</li>

<li>
👁 访客管理
</li>

<li>
⚙ 个人信息
</li>

</ul>

</div>

</template>

<script>

export default{
name:"AdminSidebar"
}

</script>

<style>

.sidebar{
width:220px;
background:white;
border-right:1px solid #eee;
padding:20px;
}

.logo{
margin-bottom:40px;
}

ul{
list-style:none;
padding:0;
}

li{
padding:12px;
cursor:pointer;
border-radius:6px;
}

li:hover{
background:#f0f2f5;
}

</style>