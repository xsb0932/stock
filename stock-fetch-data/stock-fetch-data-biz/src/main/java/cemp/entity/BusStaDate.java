package cemp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName bus_sta_date
 */
@TableName(value ="bus_sta_date")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusStaDate implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private BigDecimal begin_price;

    private BigDecimal end_price;

    private BigDecimal max_price;

    private BigDecimal min_price;

    private BigDecimal trunover;

    private Date bus_date;

    private String stock_code;

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
        BusStaDate other = (BusStaDate) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBegin_price() == null ? other.getBegin_price() == null : this.getBegin_price().equals(other.getBegin_price()))
            && (this.getEnd_price() == null ? other.getEnd_price() == null : this.getEnd_price().equals(other.getEnd_price()))
            && (this.getMax_price() == null ? other.getMax_price() == null : this.getMax_price().equals(other.getMax_price()))
            && (this.getMin_price() == null ? other.getMin_price() == null : this.getMin_price().equals(other.getMin_price()))
            && (this.getTrunover() == null ? other.getTrunover() == null : this.getTrunover().equals(other.getTrunover()))
            && (this.getBus_date() == null ? other.getBus_date() == null : this.getBus_date().equals(other.getBus_date()))
            && (this.getStock_code() == null ? other.getStock_code() == null : this.getStock_code().equals(other.getStock_code()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBegin_price() == null) ? 0 : getBegin_price().hashCode());
        result = prime * result + ((getEnd_price() == null) ? 0 : getEnd_price().hashCode());
        result = prime * result + ((getMax_price() == null) ? 0 : getMax_price().hashCode());
        result = prime * result + ((getMin_price() == null) ? 0 : getMin_price().hashCode());
        result = prime * result + ((getTrunover() == null) ? 0 : getTrunover().hashCode());
        result = prime * result + ((getBus_date() == null) ? 0 : getBus_date().hashCode());
        result = prime * result + ((getStock_code() == null) ? 0 : getStock_code().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", begin_price=").append(begin_price);
        sb.append(", end_price=").append(end_price);
        sb.append(", max_price=").append(max_price);
        sb.append(", min_price=").append(min_price);
        sb.append(", trunover=").append(trunover);
        sb.append(", bus_date=").append(bus_date);
        sb.append(", stock_code=").append(stock_code);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
