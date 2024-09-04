package cemp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName stock_all
 */
@TableName(value ="stock_all")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAll implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String stock_code;

    private String gl;

    private String name;

    private String jys;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        StockAll other = (StockAll) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStock_code() == null ? other.getStock_code() == null : this.getStock_code().equals(other.getStock_code()))
            && (this.getGl() == null ? other.getGl() == null : this.getGl().equals(other.getGl()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getJys() == null ? other.getJys() == null : this.getJys().equals(other.getJys()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStock_code() == null) ? 0 : getStock_code().hashCode());
        result = prime * result + ((getGl() == null) ? 0 : getGl().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getJys() == null) ? 0 : getJys().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", stock_code=").append(stock_code);
        sb.append(", gl=").append(gl);
        sb.append(", name=").append(name);
        sb.append(", jys=").append(jys);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
