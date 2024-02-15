package com.tinf.qmobile.database;

import static com.tinf.qmobile.network.Client.pos;

import android.util.LongSparseArray;

import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Header;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.UserUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.reactive.DataSubscription;

public class MaterialsDataProvider extends BaseDataProvider<Queryable> {
  private DataSubscription sub1;
  private DataSubscription sub2;
  public final LongSparseArray<Long> downloading;

  public MaterialsDataProvider() {
    super();
    this.downloading = new LongSparseArray<>();
  }

  @Override
  protected synchronized List<Queryable> buildList() {
    List<Queryable> list = new ArrayList<>();

    List<Matter> matters = DataBase.get().getBoxStore()
                                   .boxFor(Matter.class)
                                   .query()
                                   .order(Matter_.title_)
                                   .equal(Matter_.year_, UserUtils.getYear(pos))
                                   .and()
                                   .equal(Matter_.period_, UserUtils.getPeriod(pos))
                                   .build()
                                   .find();

    for (int i = 0; i < matters.size(); i++) {
      if (!matters.get(i).materials.isEmpty()) {
        Matter matter = matters.get(i);

        list.add(new Header(matter));
        list.add(matter);
        list.addAll(matter.materials);
      }
    }

    if (list.isEmpty())
      list.add(new Empty());

    return list;
  }

  @Override
  protected void open() {
    sub1 = DataBase.get().getBoxStore().subscribe(Material.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);

    sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);
  }

  @Override
  protected void close() {
    super.close();
    sub1.cancel();
    sub2.cancel();
  }

}
