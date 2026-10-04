package vn.edu.vhu.ltdd.a4events;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A4_231A290111";
    private static final String KEY_HISTORY = "CALC_HISTORY";

    private EditText edtSoA, edtSoB, edtCanNang, edtChieuCao;
    private TextView tvKetQua, tvLichSu, tvBmi, tvPhanLoai;

    // NC2: Danh sách lưu 5 phép tính gần nhất
    private ArrayList<String> danhSachLichSu = new ArrayList<>();

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

        // Ánh xạ View
        edtSoA = findViewById(R.id.edtSoA);
        edtSoB = findViewById(R.id.edtSoB);
        tvKetQua = findViewById(R.id.tvKetQua);
        tvLichSu = findViewById(R.id.tvLichSu);
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

        // Gán sự kiện
        btnCong.setOnClickListener(v -> tinhToan('+'));
        btnTru.setOnClickListener(v -> tinhToan('-'));

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

        // NC2: Khôi phục lịch sử sau khi xoay màn hình
        if (savedInstanceState != null) {
            danhSachLichSu = savedInstanceState.getStringArrayList(KEY_HISTORY);
            if (danhSachLichSu == null) {
                danhSachLichSu = new ArrayList<>();
            }
            capNhatGiaoDienLichSu();
        }
    }

    // NC2: Lưu danh sách lịch sử khi xoay màn hình
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_HISTORY, danhSachLichSu);
    }

    // =============== MÁY TÍNH ===============
    private void tinhToan(char phepToan) {
        String chuoiA = edtSoA.getText().toString().trim();
        String chuoiB = edtSoB.getText().toString().trim();

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

        double a, b;
        try {
            a = Double.parseDouble(chuoiA);
            b = Double.parseDouble(chuoiB);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi định dạng số", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

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

        String dongKetQua = String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", a, phepToan, b, ketQua);
        tvKetQua.setText(dongKetQua);

        // NC2: Thêm vào lịch sử (giữ tối đa 5 phép tính gần nhất)
        themVaoLichSu(dongKetQua);
    }

    private void themVaoLichSu(String phepTinh) {
        danhSachLichSu.add(0, phepTinh); // Thêm cái mới nhất lên đầu
        if (danhSachLichSu.size() > 5) {
            danhSachLichSu.remove(danhSachLichSu.size() - 1);
        }
        capNhatGiaoDienLichSu();
    }

    private void capNhatGiaoDienLichSu() {
        if (danhSachLichSu.isEmpty()) {
            tvLichSu.setText(R.string.history_placeholder);
            return;
        }
        StringBuilder sb = new StringBuilder("Lịch sử (5 phép tính gần nhất):\n");
        for (int i = 0; i < danhSachLichSu.size(); i++) {
            sb.append(i + 1).append(". ").append(danhSachLichSu.get(i)).append("\n");
        }
        tvLichSu.setText(sb.toString().trim());
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

            if (chieuCao > 3) {
                chieuCao = chieuCao / 100.0;
            }

            double bmi = canNang / (chieuCao * chieuCao);
            tvBmi.setText(String.format(Locale.getDefault(), "BMI = %.1f", bmi));

            // NC3: Cập nhật phân loại và đổi màu sắc tương ứng
            capNhatPhanLoaiBmi(bmi);

        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi định dạng BMI", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
        }
    }

    // NC3: Đổi màu theo chuẩn WHO châu Á
    private void capNhatPhanLoaiBmi(double bmi) {
        if (bmi < 18.5) {
            tvPhanLoai.setText(getString(R.string.bmi_under));
            tvPhanLoai.setTextColor(ContextCompat.getColor(this, R.color.bmi_blue));
        } else if (bmi < 23) {
            tvPhanLoai.setText(getString(R.string.bmi_normal));
            tvPhanLoai.setTextColor(ContextCompat.getColor(this, R.color.bmi_green));
        } else if (bmi < 25) {
            tvPhanLoai.setText(getString(R.string.bmi_over));
            tvPhanLoai.setTextColor(ContextCompat.getColor(this, R.color.bmi_orange));
        } else {
            tvPhanLoai.setText(getString(R.string.bmi_obese));
            tvPhanLoai.setTextColor(ContextCompat.getColor(this, R.color.bmi_red));
        }
    }
}