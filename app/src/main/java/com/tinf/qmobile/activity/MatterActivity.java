package com.tinf.qmobile.activity;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MATTER;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.ActivityMatterBinding;
import com.tinf.qmobile.fragment.matter.MatterTabsAdapter;
import com.tinf.qmobile.model.matter.Matter;

public class MatterActivity extends AppCompatActivity {
  private ActivityMatterBinding binding;
  private Matter matter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMatterBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(
        ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_cancel));

    Bundle bundle = getIntent().getExtras();

    if (bundle == null)
      finish();

    matter = DataBase.get().getBoxStore().boxFor(Matter.class).get(bundle.getLong("ID"));

    getSupportActionBar().setTitle(matter.getTitle());

    binding.pager.setAdapter(new MatterTabsAdapter(getSupportFragmentManager(), getLifecycle(),
                                                   getIntent().getExtras()));

    new TabLayoutMediator(binding.tab, binding.pager, (tab, position) -> {
      Resources resources = getBaseContext().getResources();
      switch (position) {
        case 0:
          tab.setText(resources.getString(R.string.title_home));
          break;

        case 1:
          tab.setText(resources.getString(R.string.title_notas));
          break;

        case 2:
          tab.setText(resources.getString(R.string.title_horario));
          break;

        case 3:
          tab.setText(resources.getString(R.string.title_materiais));
          break;

        case 4:
          tab.setText(resources.getString(R.string.title_class));
          break;
      }
    }).attach();

    binding.tab.setSelectedTabIndicatorColor(matter.getColor());
    binding.pager.setCurrentItem(getPage(bundle.getInt("PAGE")));
  }

  private int getPage(int page) {
    switch (page) {
      case MATTER:
        return 0;

      case JOURNAL:
        return 1;

      case SCHEDULE:
        return 2;

      case MATERIAL:
        return 3;

      case CLASS:
        return 4;
    }

    return 0;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();

    } else if (item.getItemId() == R.id.action_color) {
      if (matter != null) {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle(getString(R.string.dialog_choose_color))
            .initialColor(matter.getColor())
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(6)
            .lightnessSliderOnly()
            .setPositiveButton(getString(R.string.dialog_select),
                               (dialog, selectedColor, allColors) -> {
                                 matter.setColor(selectedColor);
                                 binding.tab.setSelectedTabIndicatorColor(matter.getColor());
                                 binding.tab.selectTab(
                                     binding.tab.getTabAt(binding.tab.getSelectedTabPosition()));
                                 DataBase.get().getBoxStore().boxFor(Matter.class).put(matter);
                               })
            .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> {})
            .build()
            .show();
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.matters, menu);
    return true;
  }

}
