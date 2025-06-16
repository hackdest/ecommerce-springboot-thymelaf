package com.ecommerce.library.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class Create_qrCode {

//tạo max base64 để tạo qrcode

    public static String generateQrCode(String data,int wid,int hei)
    {
        StringBuilder result = new StringBuilder();
        if(!data.isEmpty()){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {

                QRCodeWriter writer = new QRCodeWriter();

                BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE,wid,hei);

                BufferedImage oufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                ImageIO.write(oufferedImage,"png",os);
                result.append("data:image/png;base64,");
                result.append(new String(Base64.getEncoder().encode(os.toByteArray())));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        return result.toString();
    }
    //giải mã base64 thành ảnh
    public static BufferedImage decodeBase64ToImage(String base64String) throws Exception {
        // Remove the "data:image/png;base64," prefix (if present)
        String pureBase64 = base64String.replace("data:image/png;base64,", "");

        // Decode base64 string to byte array
        byte[] decodedBytes = Base64.getDecoder().decode(pureBase64);

        // Convert byte array to BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        return ImageIO.read(bis);
    }

//    public static void main(String[] args) {
//        String input =" anh vũ  10d";
//        System.out.println(generateQrCode(input,300,300));
//    }

    //tạo đối tướngj jsson từ dto
    public static String prettyObject(Object object){
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
//
//    @RestController
//    public class QrcodeController {
//
//        @Autowired
//        QrcodeService qrcodeService;
//
//        @PostMapping(value = "generateQRCode")
//        public ResponseEntity<byte[]> generateQRCode(@RequestBody UserLoginSdi sdi) throws Exception {
//            String basecode = qrcodeService.generateQRCode(sdi);
//            BufferedImage qrCodeImage = decodeBase64ToImage(basecode);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(qrCodeImage, "png", baos);
//            byte[] imageBytes = baos.toByteArray();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.IMAGE_PNG);
//
//            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//        }


    }


