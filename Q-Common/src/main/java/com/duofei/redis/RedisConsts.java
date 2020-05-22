package com.duofei.redis;

/**
 * jedis 执行响应
 * @author duofei
 * @date 2020/5/20
 */
public enum RedisConsts {

    OK("OK"){
        @Override
        boolean isEqual(Object v) {
            if(v instanceof  String){
                return ((String)value).equals(v);
            }
            return false;
        }
    }, ONEL(1L){
        @Override
        boolean isEqual(Object v) {
            if(v instanceof Long){
                return Long.compare((Long) v, (Long) value) == 0;
            }
            return false;
        }
    };

   Object value;

   RedisConsts(Object value){
       this.value = value;
   }

   abstract boolean isEqual(Object v);
}
