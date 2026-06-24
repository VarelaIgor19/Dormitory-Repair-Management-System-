<template>

<div class="topbar">

<div class="title">
高校宿舍管理系统
</div>

<div class="right">

<span class="status">
🟢 系统正常
</span>

<span>
{{ time }}
</span>

<button @click="logout">
退出
</button>

</div>

</div>

</template>

<script>

export default{

name:"AdminTopbar",

data(){
return{
time:new Date().toLocaleString()
}
},

methods:{

logout(){

localStorage.removeItem("token")
localStorage.removeItem("role")

alert("退出成功")

this.$router.push("/login")

}

}

}

</script>

<style>

.topbar{
height:60px;
background:white;
display:flex;
justify-content:space-between;
align-items:center;
padding:0 30px;
border-bottom:1px solid #eee;
}

.right{
display:flex;
gap:20px;
align-items:center;
}

button{
background:#409eff;
border:none;
color:white;
padding:6px 12px;
cursor:pointer;
}

</style>