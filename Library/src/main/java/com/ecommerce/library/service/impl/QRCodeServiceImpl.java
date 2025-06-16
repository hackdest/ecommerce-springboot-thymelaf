package com.ecommerce.library.service.impl;

import com.ecommerce.library.service.QrcodeService;
import com.ecommerce.library.utils.Create_qrCode;
import org.springframework.stereotype.Service;

@Service
public class QRCodeServiceImpl implements QrcodeService {
    private static final int ORDER_QR_CODE_WID = 300;
    private static final int ORDER_QR_CODE_HEI = 300;

    @Override
    public String generateQRCode(String sdi) {
        String prettyData = Create_qrCode.prettyObject(sdi);
        String qrCode = Create_qrCode.generateQrCode(prettyData, ORDER_QR_CODE_WID, ORDER_QR_CODE_HEI);
        return qrCode;
    }
}