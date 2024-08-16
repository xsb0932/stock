package cemp.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName bus_sta_date
 */
@Data
public class BusStaDate implements Serializable {
    private Integer id;

    private BigDecimal beginPrice;

    private BigDecimal endPrice;

    private BigDecimal maxPrice;

    private BigDecimal minPrice;

    private BigDecimal trunover;

    private Date busDate;

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
            && (this.getBeginPrice() == null ? other.getBeginPrice() == null : this.getBeginPrice().equals(other.getBeginPrice()))
            && (this.getEndPrice() == null ? other.getEndPrice() == null : this.getEndPrice().equals(other.getEndPrice()))
            && (this.getMaxPrice() == null ? other.getMaxPrice() == null : this.getMaxPrice().equals(other.getMaxPrice()))
            && (this.getMinPrice() == null ? other.getMinPrice() == null : this.getMinPrice().equals(other.getMinPrice()))
            && (this.getTrunover() == null ? other.getTrunover() == null : this.getTrunover().equals(other.getTrunover()))
            && (this.getBusDate() == null ? other.getBusDate() == null : this.getBusDate().equals(other.getBusDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBeginPrice() == null) ? 0 : getBeginPrice().hashCode());
        result = prime * result + ((getEndPrice() == null) ? 0 : getEndPrice().hashCode());
        result = prime * result + ((getMaxPrice() == null) ? 0 : getMaxPrice().hashCode());
        result = prime * result + ((getMinPrice() == null) ? 0 : getMinPrice().hashCode());
        result = prime * result + ((getTrunover() == null) ? 0 : getTrunover().hashCode());
        result = prime * result + ((getBusDate() == null) ? 0 : getBusDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", beginPrice=").append(beginPrice);
        sb.append(", endPrice=").append(endPrice);
        sb.append(", maxPrice=").append(maxPrice);
        sb.append(", minPrice=").append(minPrice);
        sb.append(", trunover=").append(trunover);
        sb.append(", busDate=").append(busDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}