package vn.edu.vhu.ltdd.a4events;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A4_231A290111";

    private EditText edtSoA, edtSoB, edtCanNang, edtChieuCao;
    private TextView tvKetQua, tvBmi, tvPhanLoai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // 1. Ánh xạ view
        edtSoA = findViewById(R.id.edtSoA);
        edtSoB = findViewById(R.id.edtSoB);
        tvKetQua = findViewById(R.id.tvKetQua);
        edtCanNang = findViewById(R.id.edtCanNang);
        edtChieuCao = findViewById(R.id.edtChieuCao);
        tvBmi = findViewById(R.id.tvBmi);
        tvPhanLoai = findViewById(R.id.tvPhanLoai);

        Button btnCong = findViewById(R.id.btnCong);
        Button btnTru = findViewById(R.id.btnTru);
        Button btnNhan = findViewById(R.id.btnNhan);
        Button btnChia = findViewById(R.id.btnChia);
        Button btnXoa = findViewById(R.id.btnXoa);
        Button btnTinhBmi = findViewById(R.id.btnTinhBmi);

        // 2. Gán sự kiện
        // Cách 1: Lambda cho từng nút
        btnCong.setOnClickListener(v -> tinhToan('+'));
        btnTru.setOnClickListener(v -> tinhToan('-'));

        // Cách 2: Listener dùng chung qua if-else
        View.OnClickListener chung = v -> {
            int id = v.getId();
            if (id == R.id.btnNhan) {
                tinhToan('*');
            } else if (id == R.id.btnChia) {
                tinhToan('/');
            }
        };
        btnNhan.setOnClickListener(chung);
        btnChia.setOnClickListener(chung);

        btnXoa.setOnClickListener(v -> xoaTrang());
        btnTinhBmi.setOnClickListener(v -> tinhBmi());
    }

    // =============== MÁY TÍNH ===============
    private void tinhToan(char phepToan) {
        String chuoiA = edtSoA.getText().toString().trim();
        String chuoiB = edtSoB.getText().toString().trim();

        // Lớp 1: Rỗng
        if (chuoiA.isEmpty()) {
            edtSoA.setError(getString(R.string.err_empty));
            edtSoA.requestFocus();
            return;
        }
        if (chuoiB.isEmpty()) {
            edtSoB.setError(getString(R.string.err_empty));
            edtSoB.requestFocus();
            return;
        }

        // Lớp 2: Định dạng số
        double a, b;
        try {
            a = Double.parseDouble(chuoiA);
            b = Double.parseDouble(chuoiB);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi định dạng: " + chuoiA + ", " + chuoiB, e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        // Lớp 3: Miền giá trị (chia 0)
        if (phepToan == '/' && b == 0) {
            edtSoB.setError(getString(R.string.err_divide_zero));
            edtSoB.requestFocus();
            Toast.makeText(this, R.string.err_divide_zero, Toast.LENGTH_SHORT).show();
            return;
        }

        double ketQua;
        switch (phepToan) {
            case '+': ketQua = a + b; break;
            case '-': ketQua = a - b; break;
            case '*': ketQua = a * b; break;
            default:  ketQua = a / b; break;
        }

        tvKetQua.setText(String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", a, phepToan, b, ketQua));
        Log.d(TAG, "Kết quả: " + a + " " + phepToan + " " + b + " = " + ketQua);
    }

    private void xoaTrang() {
        edtSoA.setText("");
        edtSoB.setText("");
        edtSoA.setError(null);
        edtSoB.setError(null);
        tvKetQua.setText(R.string.result_placeholder);
        edtSoA.requestFocus();
    }

    // =============== BMI ===============
    private void tinhBmi() {
        String strCanNang = edtCanNang.getText().toString().trim();
        String strChieuCao = edtChieuCao.getText().toString().trim();

        if (strCanNang.isEmpty()) {
            edtCanNang.setError(getString(R.string.err_empty));
            edtCanNang.requestFocus();
            return;
        }
        if (strChieuCao.isEmpty()) {
            edtChieuCao.setError(getString(R.string.err_empty));
            edtChieuCao.requestFocus();
            return;
        }

        try {
            double canNang = Double.parseDouble(strCanNang);
            double chieuCao = Double.parseDouble(strChieuCao);

            if (canNang <= 0) {
                edtCanNang.setError(getString(R.string.err_positive));
                edtCanNang.requestFocus();
                return;
            }
            if (chieuCao <= 0) {
                edtChieuCao.setError(getString(R.string.err_positive));
                edtChieuCao.requestFocus();
                return;
            }

            // Nghiệp vụ: Chuyển cm sang m nếu > 3
            if (chieuCao > 3) {
                chieuCao = chieuCao / 100.0;
            }

            double bmi = canNang / (chieuCao * chieuCao);
            tvBmi.setText(String.format(Locale.getDefault(), "BMI = %.1f", bmi));
            tvPhanLoai.setText(phanLoai(bmi));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi định dạng BMI", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
        }
    }

    private String phanLoai(double bmi) {
        if (bmi < 18.5) return getString(R.string.bmi_under);
        if (bmi < 23) return getString(R.string.bmi_normal);
        if (bmi < 25) return getString(R.string.bmi_over);
        return getString(R.string.bmi_obese);
    }
}