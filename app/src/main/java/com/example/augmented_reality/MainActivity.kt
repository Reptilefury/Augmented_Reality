package com.example.augmented_reality

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.SkeletonNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var  arFragment:ArFragment
    private lateinit var model: Uri
    private var renderable:ModelRenderable? = null
    private var animator: ModelAnimator?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = sceneform_fragment as ArFragment
        model = Uri.parse("model_flight.sfb")
        arFragment.setOnTapArPlaneListener{ hitResult, plane, motionEvent ->
            if(plane.type!=   Plane.Type.HORIZONTAL_UPWARD_FACING){
                return@setOnTapArPlaneListener
               }
            var anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor, model)


        }
         animate_kick_button.setOnClickListener{
             animateModel("Character|Kick")

         }
        animate_idle_button.setOnClickListener {
            animateModel(" Character | idle")
        }
        animate_boxing_button.setOnClickListener {
            animateModel("Character | Boxing")
        }

    }

    private fun animateModel(name: String) {
       animator?.let {
           if (it.isRunning){
               it.end()
           }
       }
        renderable?.let{
            modelRenderable ->
            val date = modelRenderable.getAnimationData(name)
            animator = ModelAnimator(date, modelRenderable)
            animator?.start()


        }
    }

    private fun placeObject(arFragment: ArFragment, anchor: Anchor, model:Uri?){
       ModelRenderable.builder()
           .setSource(arFragment.context, model)
           .build()
           .thenAccept{
               renderable = it
               addtoScene(arFragment , anchor, it)
           }
           .exceptionally{
               val builder = AlertDialog.Builder(this)
               builder.setMessage(it.message).setTitle(
                   "Error")
               val dialog = builder.create()
               dialog.show()
               return@exceptionally null
           }

    }
    private fun addtoScene(arFragment: ArFragment, anchor: Anchor?, it:ModelRenderable?){
        val anchorNode = AnchorNode(anchor)
        val skeletonNode = SkeletonNode()
        skeletonNode.renderable = renderable
        val node = TransformableNode(arFragment.transformationSystem)
        node.addChild(skeletonNode)
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
    }
}