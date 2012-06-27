package ro.gdg.android.domain;

public class OrderedProductExtended {
	private String productName;
	private String extraInfo;
	private String status;

	public OrderedProductExtended() {
		super();
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getExtraInfo() {
		return extraInfo == null ? "" : extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "OrderedProductExtended [productName=" + productName
				+ ", extraInfo=" + extraInfo + ", status=" + status + "]";
	}

}
