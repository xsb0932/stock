package cemp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName stock_kpi_day
 */
@TableName(value ="stock_kpi_day")
@Data
public class StockKpiDay implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String stockCode;

    private String stockName;

    private BigDecimal priceBegin;

    private BigDecimal priceEnd;

    private BigDecimal priceMax;

    private BigDecimal priceMin;

    private BigDecimal priceIncrease;

    private BigDecimal priceIncreaseRate;

    private BigDecimal per;

    private BigDecimal pbr;

    private BigDecimal mfInputQuantity;

    private BigDecimal mfInputRate;

    private BigDecimal bcInputQuantity;

    private BigDecimal bcInputRate;

    private BigDecimal sbcInputQuantity;

    private BigDecimal sbcInputRate;

    private BigDecimal mcInputQuantity;

    private String mcInputRate;

    private String scInputQuantity;

    private String scInputRate;

    private Date staDate;

    private BigDecimal turnoverRatio;

    private BigDecimal amount;

    private BigDecimal totalCapital;

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
        StockKpiDay other = (StockKpiDay) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStockCode() == null ? other.getStockCode() == null : this.getStockCode().equals(other.getStockCode()))
            && (this.getStockName() == null ? other.getStockName() == null : this.getStockName().equals(other.getStockName()))
            && (this.getPriceBegin() == null ? other.getPriceBegin() == null : this.getPriceBegin().equals(other.getPriceBegin()))
            && (this.getPriceEnd() == null ? other.getPriceEnd() == null : this.getPriceEnd().equals(other.getPriceEnd()))
            && (this.getPriceMax() == null ? other.getPriceMax() == null : this.getPriceMax().equals(other.getPriceMax()))
            && (this.getPriceMin() == null ? other.getPriceMin() == null : this.getPriceMin().equals(other.getPriceMin()))
            && (this.getPriceIncrease() == null ? other.getPriceIncrease() == null : this.getPriceIncrease().equals(other.getPriceIncrease()))
            && (this.getPriceIncreaseRate() == null ? other.getPriceIncreaseRate() == null : this.getPriceIncreaseRate().equals(other.getPriceIncreaseRate()))
            && (this.getPer() == null ? other.getPer() == null : this.getPer().equals(other.getPer()))
            && (this.getPbr() == null ? other.getPbr() == null : this.getPbr().equals(other.getPbr()))
            && (this.getMfInputQuantity() == null ? other.getMfInputQuantity() == null : this.getMfInputQuantity().equals(other.getMfInputQuantity()))
            && (this.getMfInputRate() == null ? other.getMfInputRate() == null : this.getMfInputRate().equals(other.getMfInputRate()))
            && (this.getBcInputQuantity() == null ? other.getBcInputQuantity() == null : this.getBcInputQuantity().equals(other.getBcInputQuantity()))
            && (this.getBcInputRate() == null ? other.getBcInputRate() == null : this.getBcInputRate().equals(other.getBcInputRate()))
            && (this.getSbcInputQuantity() == null ? other.getSbcInputQuantity() == null : this.getSbcInputQuantity().equals(other.getSbcInputQuantity()))
            && (this.getSbcInputRate() == null ? other.getSbcInputRate() == null : this.getSbcInputRate().equals(other.getSbcInputRate()))
            && (this.getMcInputQuantity() == null ? other.getMcInputQuantity() == null : this.getMcInputQuantity().equals(other.getMcInputQuantity()))
            && (this.getMcInputRate() == null ? other.getMcInputRate() == null : this.getMcInputRate().equals(other.getMcInputRate()))
            && (this.getScInputQuantity() == null ? other.getScInputQuantity() == null : this.getScInputQuantity().equals(other.getScInputQuantity()))
            && (this.getScInputRate() == null ? other.getScInputRate() == null : this.getScInputRate().equals(other.getScInputRate()))
            && (this.getStaDate() == null ? other.getStaDate() == null : this.getStaDate().equals(other.getStaDate()))
            && (this.getTurnoverRatio() == null ? other.getTurnoverRatio() == null : this.getTurnoverRatio().equals(other.getTurnoverRatio()))
            && (this.getAmount() == null ? other.getAmount() == null : this.getAmount().equals(other.getAmount()))
            && (this.getTotalCapital() == null ? other.getTotalCapital() == null : this.getTotalCapital().equals(other.getTotalCapital()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStockCode() == null) ? 0 : getStockCode().hashCode());
        result = prime * result + ((getStockName() == null) ? 0 : getStockName().hashCode());
        result = prime * result + ((getPriceBegin() == null) ? 0 : getPriceBegin().hashCode());
        result = prime * result + ((getPriceEnd() == null) ? 0 : getPriceEnd().hashCode());
        result = prime * result + ((getPriceMax() == null) ? 0 : getPriceMax().hashCode());
        result = prime * result + ((getPriceMin() == null) ? 0 : getPriceMin().hashCode());
        result = prime * result + ((getPriceIncrease() == null) ? 0 : getPriceIncrease().hashCode());
        result = prime * result + ((getPriceIncreaseRate() == null) ? 0 : getPriceIncreaseRate().hashCode());
        result = prime * result + ((getPer() == null) ? 0 : getPer().hashCode());
        result = prime * result + ((getPbr() == null) ? 0 : getPbr().hashCode());
        result = prime * result + ((getMfInputQuantity() == null) ? 0 : getMfInputQuantity().hashCode());
        result = prime * result + ((getMfInputRate() == null) ? 0 : getMfInputRate().hashCode());
        result = prime * result + ((getBcInputQuantity() == null) ? 0 : getBcInputQuantity().hashCode());
        result = prime * result + ((getBcInputRate() == null) ? 0 : getBcInputRate().hashCode());
        result = prime * result + ((getSbcInputQuantity() == null) ? 0 : getSbcInputQuantity().hashCode());
        result = prime * result + ((getSbcInputRate() == null) ? 0 : getSbcInputRate().hashCode());
        result = prime * result + ((getMcInputQuantity() == null) ? 0 : getMcInputQuantity().hashCode());
        result = prime * result + ((getMcInputRate() == null) ? 0 : getMcInputRate().hashCode());
        result = prime * result + ((getScInputQuantity() == null) ? 0 : getScInputQuantity().hashCode());
        result = prime * result + ((getScInputRate() == null) ? 0 : getScInputRate().hashCode());
        result = prime * result + ((getStaDate() == null) ? 0 : getStaDate().hashCode());
        result = prime * result + ((getTurnoverRatio() == null) ? 0 : getTurnoverRatio().hashCode());
        result = prime * result + ((getAmount() == null) ? 0 : getAmount().hashCode());
        result = prime * result + ((getTotalCapital() == null) ? 0 : getTotalCapital().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", stockCode=").append(stockCode);
        sb.append(", stockName=").append(stockName);
        sb.append(", priceBegin=").append(priceBegin);
        sb.append(", priceEnd=").append(priceEnd);
        sb.append(", priceMax=").append(priceMax);
        sb.append(", priceMin=").append(priceMin);
        sb.append(", priceIncrease=").append(priceIncrease);
        sb.append(", priceIncreaseRate=").append(priceIncreaseRate);
        sb.append(", per=").append(per);
        sb.append(", pbr=").append(pbr);
        sb.append(", mfInputQuantity=").append(mfInputQuantity);
        sb.append(", mfInputRate=").append(mfInputRate);
        sb.append(", bcInputQuantity=").append(bcInputQuantity);
        sb.append(", bcInputRate=").append(bcInputRate);
        sb.append(", sbcInputQuantity=").append(sbcInputQuantity);
        sb.append(", sbcInputRate=").append(sbcInputRate);
        sb.append(", mcInputQuantity=").append(mcInputQuantity);
        sb.append(", mcInputRate=").append(mcInputRate);
        sb.append(", scInputQuantity=").append(scInputQuantity);
        sb.append(", scInputRate=").append(scInputRate);
        sb.append(", staDate=").append(staDate);
        sb.append(", turnoverRatio=").append(turnoverRatio);
        sb.append(", amount=").append(amount);
        sb.append(", totalCapital=").append(totalCapital);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
