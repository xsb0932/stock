package cemp.extension;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;


public interface ExtensionMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入
     *
     * @param entityList 批量
     */
    int insertBatchSomeColumn(Collection<T> entityList);

}
