

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class XrpAccountLinesBean {
    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String account;
        private List<XrpLinesBean> lines;
        private String status;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public List<XrpLinesBean> getLines() {
            return lines;
        }

        public void setLines(List<XrpLinesBean> lines) {
            this.lines = lines;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
