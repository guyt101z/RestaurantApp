package ro.gdg.android.domain;

public class OrderedProduct {

	public static final int STATUS_ORDERED = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_DELIVERED = 2;

	private long tableBillId;
	private long productId;
	private int stateId;
	private String extraInfo;

	public OrderedProduct(long tableBillId, long productId, int stateId,
			String extraInfo) {
		super();
		this.tableBillId = tableBillId;
		this.productId = productId;
		this.stateId = stateId;
		this.extraInfo = extraInfo;
	}

	public OrderedProduct(long productId, int stateId) {
		super();
		this.productId = productId;
		this.stateId = stateId;
		extraInfo = "";
	}

	public long getTableBillId() {
		return tableBillId;
	}

	public void setTableBillId(long tableBillId) {
		this.tableBillId = tableBillId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getStateId() {
		return stateId;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	@Override
	public String toString() {
		return "OrderedProduct [tableBillId=" + tableBillId + ", productId="
				+ productId + ", stateId=" + stateId + ", extraInfo="
				+ extraInfo + "]";
	}

}
