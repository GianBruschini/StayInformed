package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gian.stayinformed.R;
import com.gian.stayinformed.model.PaisesFavoritosData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PaisesFavAdapter extends RecyclerView.Adapter<PaisesFavAdapter.PaisesFavViewHolder> {
    private Context context;
    private List<PaisesFavoritosData> mData;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onitemClick(int position);
        void onDelateClick(int position);
        void onShareClick(int position);


    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;

    }


    public PaisesFavAdapter(Context context, List<PaisesFavoritosData> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public PaisesFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.paises_item_recycler, parent, false);
        PaisesFavViewHolder evh = new PaisesFavViewHolder(v);
        return evh;

    }

    @Override
    public void onBindViewHolder(@NonNull PaisesFavViewHolder holder, int position) {
        holder.nombreText.setText(mData.get(position).getNombre());
        String casosActivos = "Casos activos: " + mData.get(position).getCasosActivos();
        holder.casosActivosText.setText(casosActivos);
        String casosConfirmados = "Confirmados: " + mData.get(position).getCasosConfirmados();
        holder.casosConfirmadosText.setText(casosConfirmados);
        String muertes= "Muertes: " + mData.get(position).getMuertes();
        holder.muertesText.setText(muertes);
        String nuevosCasos= "Nuevos casos: " + mData.get(position).getNuevosCasos();
        holder.nuevosCasosText.setText(nuevosCasos);
        String nuevasMuertes =  "Nuevas muertes: " + mData.get(position).getNuevasMuertes();
        holder.nuevasMuertesText.setText(nuevasMuertes);
        if(!mData.get(position).getFlag().isEmpty()){
            Picasso.get().load(mData.get(position).getFlag()).fit().into(holder.image_flag);
        }



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class PaisesFavViewHolder extends RecyclerView.ViewHolder {
        TextView nombreText,casosActivosText,
                casosConfirmadosText,
                muertesText,nuevosCasosText,nuevasMuertesText,imagen_delete,image_share;
        ImageView image_flag;



        public PaisesFavViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreText = itemView.findViewById(R.id.nombreText);
            casosActivosText = itemView.findViewById(R.id.casosActivosText);
            casosConfirmadosText = itemView.findViewById(R.id.casosConfirmadosText);
            muertesText = itemView.findViewById(R.id.casosMuertesText);
            nuevosCasosText = itemView.findViewById(R.id.nuevosCasosText);
            nuevasMuertesText = itemView.findViewById(R.id.nuevasMuertesText);
            imagen_delete = itemView.findViewById(R.id.image_delete);
            image_share = itemView.findViewById(R.id.image_share);
            image_flag = itemView.findViewById(R.id.imageFlag);


            imagen_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onDelateClick(position);
                        }
                    }
                }
            });

            image_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onShareClick(position);
                        }
                    }
                }
            });
        }
    }
}
