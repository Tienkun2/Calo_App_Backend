package com.dev.CaloApp.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    INVALID_KEY(1000, "Key không hợp lệ hoặc không tồn tại"),
    INVALID_TOKEN(1001, "Token không hợp lệ hoặc hết hạn"),
    INVALID_GOOGLE_TOKEN(1002, "Token GG không hợp lệ hoặc hết hạn"),
    USER_NOT_FOUND(1005, "Không tìm thấy người dùng!!!"),
    EMAIL_NOT_BLANK(1006, "Email không được để trống"),
    EMAIL_INVALID(1007, "Email không tồn tại"),
    PASSWORD_NOT_BLANK(1008, "Mật khẩu không được để trống"),
    PASSWORD_TOO_SHORT(1009, "Mật khẩu phải có ít nhất 6 ký tự"),
    NAME_NOT_BLANK(1010, "Tên không được để trống"),
    NAME_TOO_LONG(1011, "Tên không được quá 100 ký tự"),
    AGE_POSITIVE(1012, "Tuổi phải là số dương"),
    AGE_MIN(1013, "Tuổi phải ít nhất 1"),
    AGE_MAX(1014, "Tuổi không được vượt quá 150"),
    HEIGHT_POSITIVE(1015, "Chiều cao phải là số dương"),
    HEIGHT_MIN(1016, "Chiều cao phải ít nhất 50 cm"),
    HEIGHT_MAX(1017, "Chiều cao không hợp lệ (tối đa 300 cm)"),
    HEIGHT_DECIMAL(1018, "Chiều cao chỉ được có 1 chữ số thập phân"),
    WEIGHT_POSITIVE(1019, "Cân nặng phải là số dương"),
    WEIGHT_MIN(1020, "Cân nặng phải ít nhất 20 kg"),
    WEIGHT_MAX(1021, "Cân nặng không hợp lệ (tối đa 500 kg)"),
    WEIGHT_DECIMAL(1022, "Cân nặng chỉ được có 1 chữ số thập phân"),
    FIRST_WEIGHT_POSITIVE(1023, "Cân nặng ban đầu phải là số dương"),
    FIRST_WEIGHT_MIN(1024, "Cân nặng ban đầu phải ít nhất 20 kg"),
    FIRST_WEIGHT_MAX(1025, "Cân nặng ban đầu không được vượt quá 500 kg"),
    FIRST_WEIGHT_DECIMAL(1026, "Cân nặng ban đầu chỉ được có 1 chữ số thập phân"),
    GENDER_INVALID(1027, "Giới tính chỉ được là Nam hoặc Nu"),
    INVALID_OTP(1030, "Mã otp sai hoặc không hợp tồn tại"),
    OTP_EXPIRED(1031, "Mã otp đã hết hạn"),
    CALORIES_POSITIVE(1032, "Lượng calo hàng ngày phải là số dương"),
    CALORIES_MIN(1033, "Lượng calo hàng ngày phải ít nhất 500 kcal"),
    CALORIES_DECIMAL(1034, "Lượng calo chỉ được có 1 chữ số thập phân"),
    PASSWORD_NOT_MATCH(1035, "Mật khẩu xác nhận không trùng khớp"),
    PASSWORDRREST_TOKEN_INVALID(1036, "Token sai hoặc không tồn tại"),
    TOKEN_EXPIRED(1037, "Token hết hạn"),
    FOOD_NOT_FOUND(1038, "Không tìm thấy dữ liệu của thức ăn"),
    FOOD_EXISTED(1039, "Món ăn đã tồn tại trong database"),
    MEAL_LOG_NOT_FOUND(1040, "Không tìm thấy dữ liệu bữa ăn"),
    BARCODE_NOT_IN_DATABASE(2000, "không tìm thấy thông tin sản phẩm trong database"),
    VALIDATION_FAILED(9003, "Dữ liệu nhập vào không hợp lệ");

    private int code;
    private String message;
}