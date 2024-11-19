package teamnova.elite_gear.model;

public  class PaymentResponse {
    private String merchantId;
    private String hash;

    public PaymentResponse(String merchantId, String hash) {
        this.merchantId = merchantId;
        this.hash = hash;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getHash() {
        return hash;
    }
}