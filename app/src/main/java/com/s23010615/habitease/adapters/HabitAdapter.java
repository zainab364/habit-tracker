package com.s23010615.habitease.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010615.habitease.R;
import com.s23010615.habitease.activities.EditHabitActivity;
import com.s23010615.habitease.activities.HabitDetailActivity;
import com.s23010615.habitease.database.DBHelper;
import com.s23010615.habitease.models.Habit;
import java.util.List;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private Context context;
    private List<Habit> habitList;

    public HabitAdapter(Context context, List<Habit> habitList) {
        this.context = context;
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }
    private void showCompletionDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_habit_completed);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Auto dismiss after 1.5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 1500);

        dialog.show();
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        // Bind data
        holder.habitName.setText(habit.getName());
        holder.habitTime.setText(habit.getTime());
        holder.checkBox.setOnCheckedChangeListener(null); // doubt
        holder.checkBox.setChecked(habit.isCompleted());

        // Home-only indicator visibility
        holder.homeOnlyIndicator.setVisibility(habit.isHomeOnly() ? View.VISIBLE : View.GONE);

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HabitDetailActivity.class);
            intent.putExtra("habit_id", habit.getId());
            context.startActivity(intent);
        });

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditHabitActivity.class);
            intent.putExtra("habit_id", habit.getId());
            context.startActivity(intent);
        });

        // Update status text based on initial state
        holder.habitStatus.setText(habit.isCompleted() ? "Completed" : "Incomplete");

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update model
            habit.setCompleted(isChecked);
            // Update the habit completion status in DB
            DBHelper dbHelper = new DBHelper(context);
            dbHelper.updateHabitCompletion(habit.getId(), isChecked);

            if (isChecked) {
                holder.habitStatus.setText("Completed");
                holder.habitStatus.setTextColor(ContextCompat.getColor(context, R.color.Green));
                showCompletionDialog();
            } else {
                holder.habitStatus.setText("Incomplete");
                holder.habitStatus.setTextColor(ContextCompat.getColor(context, R.color.Red));
            }
        });

        // Navigate to Relevant Habit Details Page
        holder.mainContent.setOnClickListener(v -> {
            Intent intent = new Intent(context, HabitDetailActivity.class);
            intent.putExtra("habitId", habit.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public void updateHabits(List<Habit> newHabits) {
        this.habitList.clear();
        this.habitList.addAll(newHabits);
        notifyDataSetChanged();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName, habitTime,habitStatus;
        CheckBox checkBox;
        ImageButton editButton;
        View homeOnlyIndicator,mainContent;


        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habitName);
            habitTime = itemView.findViewById(R.id.habitTime);
            checkBox = itemView.findViewById(R.id.habitCheckBox);
            editButton = itemView.findViewById(R.id.editHabitButton);
            mainContent = itemView.findViewById(R.id.mainContent);
            habitStatus = itemView.findViewById(R.id.habitStatus);
            homeOnlyIndicator = itemView.findViewById(R.id.homeOnlyIndicator);  // I think no need
        }
    }
}

